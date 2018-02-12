package com.android.msqhealthpoc1.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.PaymentGatewayWebView;
import com.android.msqhealthpoc1.adapters.CartDialogListAdapter;
import com.android.msqhealthpoc1.model.CartItem;
import com.android.msqhealthpoc1.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sihlemabaleka on 9/24/17.
 */

public class CartDialogs extends DialogFragment {


    List<CartItem> items = new ArrayList<>();
    private DatabaseReference mDatabase;

    TextView mCartItemCount;
    RecyclerView mList;
    Button btnCheckOut, btnClearAll;

    CartDialogListAdapter adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog view = super.onCreateDialog(savedInstanceState);
        view.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.setContentView(R.layout.cart_layout);

        Window window = view.getWindow();
        WindowManager.LayoutParams dlp = window.getAttributes();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dlp.gravity = Gravity.TOP;
        window.setAttributes(dlp);

        btnCheckOut = (Button) view.findViewById(R.id.checkout);
        btnClearAll = (Button) view.findViewById(R.id.clearAll);

        mCartItemCount = (TextView) view.findViewById(R.id.cart_total_count);
        mList = (RecyclerView) view.findViewById(R.id.list);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();

            mDatabase.child("users").child(user.getUid()).child("cart").child("cart-items").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.hasChildren()) {
                            items.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                //Create cart item and set initial values
                                CartItem item = new CartItem();
                                item.setQuantity(Integer.parseInt(String.valueOf((long) snapshot.child("quantity").getValue())));
                                item.setOwner_id(uid);

                                //Create and set product object
                                Product product = new Product();
                                product.setCode(snapshot.child("product").child("code").getValue().toString());
                                product.setConsumables(snapshot.child("product").child("consumables").getValue().toString());
                                product.setDescription(snapshot.child("product").child("description").getValue().toString());
                                product.setPrice(Double.parseDouble(snapshot.child("product").child("price").getValue().toString()));
                                product.setTrueImageUrl(snapshot.child("product").child("trueImageUrl").getValue().toString());
                                product.setUnit_of_messuremeant(snapshot.child("product").child("unit_of_messuremeant").getValue().toString());
                                //Add product to cartItem
                                item.setProduct(product);
                                if (item != null) {
                                    items.add(item);
                                } else {
                                    System.out.println("No Item found");
                                }
                            }

                            adapter = new CartDialogListAdapter(items, getActivity(), dataSnapshot);
                            mList.setAdapter(adapter);

                            btnCheckOut.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss();
                                    startActivity(new Intent(getActivity(), PaymentGatewayWebView.class));
                                }
                            });

                            btnClearAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Cart cleared successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getActivity(), "Cart clearing failed", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "Connection Error. Please check internet connection", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            });
        }

        return view;

    }

}

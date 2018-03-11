package com.android.msqhealthpoc1.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.ConfirmCheckoutActivity;
import com.android.msqhealthpoc1.activities.ContactUsActivity;
import com.android.msqhealthpoc1.activities.MainActivity;
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

import org.apache.http.util.EncodingUtils;

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

    ProgressDialog mProgressDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog view = super.onCreateDialog(savedInstanceState);
        view.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.setContentView(R.layout.cart_layout);

        Window window = view.getWindow();
        WindowManager.LayoutParams dlp = window.getAttributes();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dlp.gravity = Gravity.TOP;
        window.setAttributes(dlp);

        mProgressDialog = new ProgressDialog(getContext());

        btnCheckOut = (Button) view.findViewById(R.id.checkout);
        btnClearAll = (Button) view.findViewById(R.id.clearAll);

        mCartItemCount = (TextView) view.findViewById(R.id.cart_total_count);
        mList = (RecyclerView) view.findViewById(R.id.list);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();

            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").child("cart-items").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    double _amount = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem cartItem = new CartItem();
                        cartItem.setQuantity(Integer.parseInt(snapshot.child("quantity").getValue().toString()));
                        _amount = _amount + ((Double.parseDouble(String.valueOf(snapshot.child("product").child("price").getValue()))) * (Integer.parseInt(snapshot.child("quantity").getValue().toString())));

                        System.out.println("Amount is " + _amount);
                    }

                    final double amount = _amount;
                    double valueRounded = Math.round(amount * 100D) / 100D;
                    mCartItemCount.setText(String.valueOf("Total R" + valueRounded));
                    System.out.println("FINAL Amount is: " + valueRounded);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabase.child("users").child(user.getUid()).child("cart").child("cart-items").addValueEventListener(new ValueEventListener() {
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

                            final DatabaseReference mCheckDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").child("cart-items");
                                btnCheckOut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (isNetworkAvailable()) {
                                            mCheckDatabase.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    try {
                                                        if (dataSnapshot.exists()) {
                                                            startActivity(new Intent(getActivity(), ConfirmCheckoutActivity.class));
                                                            dismiss();
                                                        } else {
                                                            Toast.makeText(getContext(), "You can't checkout an empty cart", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (Exception e){
                                                        System.out.println("ERROR: " + e.getMessage());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        } else if (!isNetworkAvailable()) {

                                            Snackbar snack = Snackbar.make(view.findViewById(R.id.relative_layout), "No Connection Available, please check your internet settings and try again.",
                                                    Snackbar.LENGTH_INDEFINITE).setDuration(1000);
                                            snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
                                            View view = snack.getView();
                                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                                            params.gravity = Gravity.TOP;
                                            view.setLayoutParams(params);
                                            snack.show();
                                        }
                                    }
                                });

                            btnClearAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mProgressDialog.setMessage("Clearing Cart ...");
                                    mProgressDialog.show();
                                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Cart cleared successfully", Toast.LENGTH_SHORT).show();
                                                    mProgressDialog.dismiss();
                                                    dismiss();
                                                } else {
                                                    Toast.makeText(getContext(), "Cart clearing failed", Toast.LENGTH_SHORT).show();
                                                    mProgressDialog.dismiss();
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "Connection Error. Please check internet connection", Toast.LENGTH_SHORT).show();
                                                mProgressDialog.dismiss();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

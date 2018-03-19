package com.msqhealth.main.dialogs;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.msqhealth.main.R;
import com.msqhealth.main.activities.ConfirmCheckoutActivity;
import com.msqhealth.main.adapters.CartDialogListAdapter;
import com.msqhealth.main.model.CartItem;
import com.msqhealth.main.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sihlemabaleka on 9/24/17.
 */

public class CartDialogs extends DialogFragment {


    List<CartItem> items = new ArrayList<>();
    private DatabaseReference mDatabase;

    TextView mCartItemCount, mText_view;
    RecyclerView mList;
    Button btnCheckOut, btnClearAll;
    ImageButton mImageButtonEmptyCart;

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
        mText_view = view.findViewById(R.id.text_view);

        mImageButtonEmptyCart = view.findViewById(R.id.imgb_cart);

        mCartItemCount = (TextView) view.findViewById(R.id.cart_total_count);
        mList = (RecyclerView) view.findViewById(R.id.list);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();

            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("carts").child("pending").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    double _amount = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            CartItem cartItem = new CartItem();
                            cartItem.setQuantity(Integer.parseInt(snapshot.child("quantity").getValue().toString()));
                            _amount = _amount + ((Double.parseDouble(String.valueOf(snapshot.child("product").child("price").getValue()))) * (Integer.parseInt(snapshot.child("quantity").getValue().toString())));

                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                    DecimalFormat df = new DecimalFormat("##.##");
                    final double amount = _amount;
                    mCartItemCount.setText("R" + String.valueOf(df.format(amount)).replace(",", "."));


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("carts").child("pending").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("SNAP: " + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() == 0) {
                        btnClearAll.setVisibility(View.GONE);
                        mCartItemCount.setVisibility(View.GONE);
                        btnCheckOut.setText("Browse for Shopping!");
                        mImageButtonEmptyCart.setVisibility(View.VISIBLE);
                        mText_view.setVisibility(View.VISIBLE);
                        btnCheckOut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dismiss();
                            }
                        });
                    } else {
                        btnCheckOut.setText("Checkout");
                        btnClearAll.setVisibility(View.VISIBLE);
                        mCartItemCount.setVisibility(View.VISIBLE);
                        btnCheckOut.setEnabled(true);
                        mImageButtonEmptyCart.setVisibility(View.GONE);
                        mText_view.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabase.child("users").child(user.getUid()).child("carts").child("pending").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.hasChildren()) {
                            items.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                //Create cart item and set initial values
                                try {
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
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }

                            adapter = new CartDialogListAdapter(items, getActivity(), dataSnapshot);
                            mList.setAdapter(adapter);

                            final DatabaseReference mCheckDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("carts").child("pending");
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
                                                    }
                                                } catch (Exception e) {
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
                                                    mProgressDialog.dismiss();
                                                    dismiss();
                                                } else {
                                                    mProgressDialog.dismiss();
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "Please check internet connection", Toast.LENGTH_SHORT).show();
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

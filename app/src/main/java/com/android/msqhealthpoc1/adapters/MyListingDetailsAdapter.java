package com.android.msqhealthpoc1.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyListingDetailsAdapter extends RecyclerView.Adapter<MyListingDetailsAdapter.ViewHolder> {

    private final List<Product> mValues;
    private Activity activity;

    public MyListingDetailsAdapter(List<Product> items, Activity activity) {
        this.mValues = items;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listing_details_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        System.out.println(mValues.get(position).description);
        holder.mItem = mValues.get(position);
        holder.mDescriptionView.setText(mValues.get(position).description);
        holder.mPricingUnitView.setText(mValues.get(position).unit_of_messuremeant);
        holder.mPricingView.setText(String.valueOf("R" + mValues.get(position).price));
        holder.mCodeview.setText(mValues.get(position).code);

        System.out.println("Print COde" + mValues.get(position).getCode());

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cart").child("cart-items");
//        mCheckReference.child("cart-items").child(mValues.get(position).getCode()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
//                        System.out.println("EXIST");
//                        holder.btnAddToCart.setAlpha(.5f);
//                        holder.btnAddToCart.setClickable(false);
//                        holder.btnAddToCart.setEnabled(false);
//                    }
//                } else if (!dataSnapshot.exists()) {
//                    System.out.println("EXIST NOT");
//                    holder.btnAddToCart.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        //Button to decrement the items in the cart
        holder.mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                quant[0] = (long) dataSnapshot.child("quantity").getValue();
                int quantity = Integer.parseInt(holder.mQuantity.getText().toString());

                if (quantity >= 2){
                    quantity--;
                    System.out.println("GET QUANTITY: " + quantity);
                    holder.mQuantity.setText(String.valueOf(quantity));
                }
            }
        });

        //Button to increment the items in the cart
        holder.mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[][] quantity = {{Integer.parseInt(holder.mQuantity.getText().toString())}};
                quantity[0][0]++;
                System.out.println("GET QUANTITY: " + quantity[0][0]);
                holder.mQuantity.setText(String.valueOf(quantity[0][0]));
            }
        });

        mDatabase.child(mValues.get(position).getCode()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long quant = 0;
                if (dataSnapshot.exists()) {
                    quant = (long) dataSnapshot.child("quantity").getValue();
                    final int[][] quantity = {{Integer.parseInt(holder.mQuantity.getText().toString())}};

                    holder.mQuantity.setText(String.valueOf(quant));

                    //Button to decrement the items in the cart
//                    holder.mIncrementButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            quantity[0][0]++;
//                            System.out.println("GET QUANTITY: " + quantity[0][0]);
//                            holder.mQuantity.setText(String.valueOf(quantity[0][0]));
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child(mValues.get(position).code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.mQuantity.getText().toString() == "") {
                                holder.mQuantity.setError("Please add quantity...");
                                return;
                            }

                            CartItem item = dataSnapshot.getValue(CartItem.class);
                            try {
                                if (holder.mQuantity.getText() != null) {
                                    item.setQuantity(item.getQuantity() + Integer.parseInt(holder.mQuantity.getText().toString()));
                                } else {
                                    item.setQuantity(item.getQuantity() + 1);
                                    return;
                                }
                            } catch (Exception ex) {
                                holder.mQuantity.setError("Please add a quantity");
                                return;
                            }
                            Map<String, Object> postValues = item.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/cart/cart-items/" + mValues.get(position).getCode(), postValues);
                            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar snack = Snackbar.make(holder.mLinearLayout, "Item/s added to cart",
                                                Snackbar.LENGTH_INDEFINITE).setDuration(2000);
                                        snack.getView().setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_green_dark));
                                        View view = snack.getView();
                                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                                        params.gravity = Gravity.BOTTOM;
                                        view.setLayoutParams(params);
                                        snack.show();
                                    } else {
                                        task.getException().printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.mQuantity.getText().toString() == "") {
                                holder.mQuantity.setError("Please add quantity...");
                                return;
                            }
                            CartItem cartItem = new CartItem();
                            cartItem.setOwner_id(user.getUid());
                            cartItem.setProduct(holder.mItem);
                            cartItem.setQuantity(1);
                            try {
                                if (holder.mQuantity.getText() != null) {
                                    cartItem.setQuantity(Integer.parseInt(holder.mQuantity.getText().toString()));
                                } else {
                                    cartItem.setQuantity(1);
                                }
                            } catch (Exception ex) {
                                holder.mQuantity.setError("Please add a quantity");
                                return;
                            }

                            Map<String, Object> postValues = cartItem.toMap();


                            if (isNetworkAvailable()) {

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/carts/pending/" + mValues.get(position).getCode(), postValues);

                                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar snack = Snackbar.make(holder.mLinearLayout, "Item/s added to cart",
                                                    Snackbar.LENGTH_INDEFINITE).setDuration(1000);
                                            snack.getView().setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_green_dark));
                                            View view = snack.getView();
                                            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                                            params.gravity = Gravity.BOTTOM;
                                            view.setLayoutParams(params);
                                            snack.show();
//                                        Toast.makeText(activity, "Item added to cart", Toast.LENGTH_SHORT).show();
                                        } else {
                                            task.getException().printStackTrace();
                                        }
                                    }
                                });

                            } else if (!isNetworkAvailable()) {

                                Snackbar snack = Snackbar.make(holder.mLinearLayout, "No Connection Available, please check your internet settings and try again.",
                                        Snackbar.LENGTH_INDEFINITE).setDuration(1000);
                                snack.getView().setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
                                View view = snack.getView();
                                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                view.setLayoutParams(params);
                                snack.show();
                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    databaseError.toException().printStackTrace();
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showDialog() {
        View mView = LayoutInflater.from(activity).inflate(R.layout.deleted_custom_dialog, null);

        TextView successView = mView.findViewById(R.id.tv_success);
        android.support.v7.app.AlertDialog.Builder aBuilder = new android.support.v7.app.AlertDialog.Builder(activity, R.style.CustomDialog);
        aBuilder.setView(mView);

        successView.setText("Successfully Added to Cart!");

        final android.support.v7.app.AlertDialog alert = aBuilder.create();
        alert.show();

        // Hide after some seconds
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 1500);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDescriptionView;
        public final TextView mPricingUnitView;
        public final TextView mPricingView;
        public final Button btnAddToCart;
        public final TextView mCodeview;
        public final EditText mQuantity;
        public final ImageButton mIncrementButton;
        public final ImageButton mDecrementButton;
        public final LinearLayout mLinearLayout;
        public Product mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDescriptionView = view.findViewById(R.id.info_description);
            mPricingUnitView = view.findViewById(R.id.info_unit);
            mPricingView = view.findViewById(R.id.info_price);
            btnAddToCart = view.findViewById(R.id.addToCart);
            mCodeview = view.findViewById(R.id.nappi_code);
            mLinearLayout = view.findViewById(R.id.linear);
            mDecrementButton = view.findViewById(R.id.btn_decrement);
            mIncrementButton = view.findViewById(R.id.btn_increment);
            mQuantity = view.findViewById(R.id.cart_item_quantity);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionView.getText() + "'";
        }
    }
}

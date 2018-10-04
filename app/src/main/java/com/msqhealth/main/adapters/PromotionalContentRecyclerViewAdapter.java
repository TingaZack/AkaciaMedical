package com.msqhealth.main.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msqhealth.main.R;
import com.msqhealth.main.model.CartItem;
import com.msqhealth.main.model.Product;
import com.bumptech.glide.Glide;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PromotionalContentRecyclerViewAdapter extends RecyclerView.Adapter<PromotionalContentRecyclerViewAdapter.ViewHolder> {

    private final List<Product> mValues;
    private Activity activity;
    private int final_difference;
    private FirebaseUser user;
    private DataSnapshot snap;

    public PromotionalContentRecyclerViewAdapter(List<Product> items, Activity activity) {
        mValues = items;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promotions_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mConsumableView.setText(mValues.get(position).consumables);
        holder.mDescriptionView.setText(mValues.get(position).description.toLowerCase());
        holder.mPercentageView.setText(activity.getString(R.string.percentage, mValues.get(position).percentage));
        holder.mEndDate.setText(mValues.get(position).end_date);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(mValues.get(position).end_date);
            System.out.println("D A T E" + date);

            int diff = (int) (date.getTime() - new Date().getTime());
            int diffaddone = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            final_difference = diffaddone + 1;
            System.out.println("PRINT: " + final_difference);

            if (final_difference == 1) {
                holder.mEndDate.setText(activity.getString(R.string.promo_one_day, final_difference));
                holder.mEndDate.setTextColor(Color.RED);
            } else if (final_difference == 0){
                holder.mEndDate.setText(R.string.promo_zero_day);
            } else {
                holder.mEndDate.setText(activity.getString(R.string.promo_days, final_difference));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        DecimalFormat df = new DecimalFormat("##.00");

        holder.mPriceView.setText(activity.getString(R.string.rand, String.valueOf(df.format(mValues.get(position).price))));
        holder.mCodeView.setText(mValues.get(position).code);
        holder.mPricingUnitView.setText(String.valueOf(mValues.get(position).unit_of_messuremeant.toLowerCase()));
        Glide.with(activity).load(mValues.get(position).trueImageUrl).into(holder.mProductImage);

        System.out.println("PR___" + String.valueOf(mValues.get(position).price));

        user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                snap = dataSnapshot;
                if (user != null && dataSnapshot.hasChild("debtorCode")) {
                    DatabaseReference mCheckReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart");
//            mCheckReference.child("cart-items").child(mValues.get(position).getCode()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
//                            System.out.println("EXIST");
//                            holder.btnAddToCart.setAlpha(.5f);
//                            holder.btnAddToCart.setClickable(false);
//                            holder.btnAddToCart.setEnabled(false);
//                        }
//                    } else if (!dataSnapshot.exists()) {
//                        System.out.println("EXIST NOT");
//                        holder.btnAddToCart.setEnabled(true);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });


                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cart").child("cart-items");

                    //Button to decrement the items in the cart
                    holder.mDecrementButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//                quant[0] = (long) dataSnapshot.child("quantity").getValue();
                            int quantity = Integer.parseInt(holder.mQuantity.getText().toString());

                            if (quantity >= 2) {
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
                                System.out.println("NEW : " + quant);

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

                    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("carts").child("pending").child(mValues.get(position).code);

                    mDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //Check if the account has been verified by checking if the debtors code exists.
                                        if (snap.hasChild("debtorCode")) {

                                            if (holder.mQuantity.getText().toString() == "") {
                                                holder.mQuantity.setError(activity.getString(R.string.added_product_error));
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
                                                holder.mQuantity.setError(activity.getString(R.string.added_product_error));
                                                return;
                                            }
                                            Map<String, Object> postValues = item.toMap();

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put("/carts/pending/" + mValues.get(position).getCode(), postValues);
                                            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Snackbar snack = Snackbar.make(holder.mRelativeLayout, activity.getString(R.string.added_product_),
                                                                Snackbar.LENGTH_INDEFINITE).setDuration(2000);
                                                        snack.getView().setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_green_dark));
                                                        View view = snack.getView();
                                                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                                                        params.gravity = Gravity.BOTTOM;
                                                        view.setLayoutParams(params);
                                                        snack.show();
//                                            Toast.makeText(activity, "Item added to cart", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        task.getException().printStackTrace();
                                                    }
                                                }
                                            });
                                        } else {
                                            //if the account is not verified, show a popup that says so.
                                            showDialog();
                                        }
                                    }
                                });
                            } else {
                                holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Check if the account has been verified by checking if the debtors code exists.
                                        if (snap.hasChild("debtorCode")) {
                                            if (holder.mQuantity.getText().toString() == "") {
                                                holder.mQuantity.setError(activity.getString(R.string.added_product_error));
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
                                                holder.mQuantity.setError(activity.getString(R.string.added_product_error));
                                                return;
                                            }

                                            Map<String, Object> postValues = cartItem.toMap();

                                            String key = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").push().getKey();


                                            if (isNetworkAvailable()) {
                                                Map<String, Object> childUpdates = new HashMap<>();
                                                childUpdates.put("/carts/pending/" + mValues.get(position).getCode(), postValues);

                                                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Snackbar snack = Snackbar.make(holder.mRelativeLayout, activity.getString(R.string.added_product_),
                                                                    Snackbar.LENGTH_INDEFINITE).setDuration(2000);
                                                            snack.getView().setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_green_dark));
                                                            View view = snack.getView();
                                                            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                                                            params.gravity = Gravity.BOTTOM;
                                                            view.setLayoutParams(params);
                                                            snack.show();
//                                                Toast.makeText(activity, "Item added to cart", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            task.getException().printStackTrace();
                                                        }
                                                    }
                                                });
                                            } else if (!isNetworkAvailable()) {
                                                Snackbar snack = Snackbar.make(holder.mRelativeLayout, activity.getString(R.string.no_connection),
                                                        Snackbar.LENGTH_INDEFINITE).setDuration(1000);
                                                snack.getView().setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
                                                View view = snack.getView();
                                                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                                                params.gravity = Gravity.TOP;
                                                view.setLayoutParams(params);
                                                snack.show();
                                            }
                                        } else {
                                            //if the account is not verified, show a popup that says so.
                                            showDialog();
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showDialog() {
        View mView = LayoutInflater.from(activity).inflate(R.layout.deleted_custom_dialog, null);
        android.support.v7.app.AlertDialog.Builder aBuilder = new android.support.v7.app.AlertDialog.Builder(activity, R.style.CustomDialog);
        aBuilder.setView(mView);
        android.support.v7.app.AlertDialog alert = aBuilder.create();
        alert.show();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mProductImage;
        public final TextView mConsumableView;
        public final TextView mPriceView, mDescriptionView, mPricingUnitView, mPercentageView, mCodeView;
        public final Button btnAddToCart;
        public final EditText mQuantity;
        public final TextView mEndDate;
        RelativeLayout mRelativeLayout;
        public final ImageButton mIncrementButton;
        public final ImageButton mDecrementButton;
        public Product mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProductImage = (ImageView) view.findViewById(R.id.img_product);
            mConsumableView = (TextView) view.findViewById(R.id.tv_consumable);
            mPriceView = (TextView) view.findViewById(R.id.tv_price);
            mPricingUnitView = view.findViewById(R.id.info_unit);
            mCodeView = view.findViewById(R.id.tv_code);
            btnAddToCart = view.findViewById(R.id.addToCart);
            mQuantity = view.findViewById(R.id.cart_item_quantity);
            mPercentageView = view.findViewById(R.id.tv_percentage);
            mRelativeLayout = view.findViewById(R.id.relative_layout);
            mDecrementButton = view.findViewById(R.id.btn_decrement);
            mIncrementButton = view.findViewById(R.id.btn_increment);
            mDescriptionView = (TextView) view.findViewById(R.id.tv_description);
            mEndDate = (TextView) view.findViewById(R.id.tv_end_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mConsumableView.getText() + "'";
        }
    }
}

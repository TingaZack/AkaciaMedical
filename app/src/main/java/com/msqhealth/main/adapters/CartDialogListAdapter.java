package com.msqhealth.main.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.msqhealth.main.R;
import com.msqhealth.main.model.CartItem;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sihlemabaleka on 9/24/17.
 */

public class CartDialogListAdapter extends RecyclerView.Adapter<CartDialogListAdapter.ViewHolder> {

    private final List<CartItem> mValues;
    private Activity activity;
    long total = 0;
    long my_sum = 0;
    private DataSnapshot dataSnapshot;

    public CartDialogListAdapter(List<CartItem> items, Activity activity, DataSnapshot dataSnapshot) {
        mValues = items;
        this.activity = activity;
        this.dataSnapshot = dataSnapshot;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_layout_item_new, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mAuth = FirebaseAuth.getInstance();

        if (holder.mAuth.getCurrentUser() != null) {
            holder.userid = holder.mAuth.getCurrentUser().getUid();

            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(holder.userid).child("carts").child("pending");
            final DatabaseReference mDatabaseQuantity = FirebaseDatabase.getInstance().getReference().child("users").child(holder.userid).child("carts").child("pending");

            System.out.println(mValues.get(position).getProduct().consumables);

            DecimalFormat decimalDigitsFormat = new DecimalFormat("##.00");

            Glide.with(activity).load(mValues.get(position).getProduct().trueImageUrl).into(holder.mCartItemImage);
            holder.mCartItemTitle.setText(mValues.get(position).getProduct().consumables);
            holder.mCartItemQuantity.setText(String.valueOf(mValues.get(position).getQuantity()));
            holder.mDescTextView.setText(mValues.get(position).getProduct().description.toLowerCase());
            holder.mCodeTextView.setText(mValues.get(position).getProduct().code);
            holder.mPricingUnitTextView.setText(" Qty: " + String.valueOf(mValues.get(position).getProduct().unit_of_messuremeant.toLowerCase()).replace(",","."));
            final String tot = String.valueOf(decimalDigitsFormat.format(mValues.get(position).getProduct().price * Integer.parseInt(holder.mCartItemQuantity.getText().toString())));
            holder.mCartItemTotal.setText(tot.replace(",", "."));


            mDatabase.child(mValues.get(position).getProduct().getCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final long[] quant = {0};
                    if (dataSnapshot.exists()) {
                        quant[0] = (long) dataSnapshot.child("quantity").getValue();
                        final int[][] quantity = {{Integer.parseInt(holder.mCartItemQuantity.getText().toString())}};
                        total = total + quant[0];

                        //Button to decrement the items in the cart
                        holder.btnDecrementCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (Integer.parseInt(holder.mCartItemQuantity.getText().toString()) >= 2) {
                                    quantity[0][0]--;
                                    System.out.println("GET QUANTITY: " + quantity[0][0]);
                                    holder.mCartItemQuantity.setText(String.valueOf(quantity[0][0]));
                                }
                            }
                        });

                        //Button to decrement the items in the cart
                        holder.btnIncrementCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                quantity[0][0]++;
                                System.out.println("GET QUANTITY: " + quantity[0][0]);
                                holder.mCartItemQuantity.setText(String.valueOf(quantity[0][0]));
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            my_sum = total;
            System.out.println("WHOLE SUM: " + my_sum);

            mDatabaseQuantity.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        System.out.println("Q: " + dataSnapshot.child("quantity").getValue());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            holder.btnRemoveCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase.child(mValues.get(position).getProduct().getCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                dataSnapshot.getRef().removeValue();
                                mValues.remove(position);
                                notifyDataSetChanged();

//                                showDialog();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

//            countCart(holder.mCartItemTotal);

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        DecimalFormat decimalDigitsFormat = new DecimalFormat("##.00");
                        System.out.println(charSequence);

                        final StringBuilder sb = new StringBuilder(charSequence.length());
                        sb.append(charSequence);

                        Double amount;

                        try {
                            amount = mValues.get(position).getProduct().price * Double.parseDouble(sb.toString());

                            holder.mCartItemTotal.setText(String.valueOf(decimalDigitsFormat.format(amount)).replace(",","."));

                            mValues.get(position).setQuantity(Integer.parseInt(sb.toString()));

                            Map<String, Object> postValues = mValues.get(position).toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/carts/pending/" + mValues.get(position).getProduct().code, postValues);
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(childUpdates);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            };
            holder.mCartItemQuantity.addTextChangedListener(watcher);
        }
    }

    private void showDialog() {
        View mView = LayoutInflater.from(activity).inflate(R.layout.deleted_custom_dialog, null);

        TextView successView = mView.findViewById(R.id.tv_success);
        android.support.v7.app.AlertDialog.Builder aBuilder = new android.support.v7.app.AlertDialog.Builder(activity, R.style.CustomDialog);
        aBuilder.setView(mView);

        successView.setText("Successfully Removed!");

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

        handler.postDelayed(runnable, 1000);

    }

    public double countCart(TextView textViewPrice) {
        double sum = Double.parseDouble(textViewPrice.getText().toString());
//        total = total + sum;
//        System.out.println("Total Price: " + total);

        return sum;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mCartItemImage;
        public final TextView mCartItemTitle;
        public final TextView mCartItemTotal;
        public final EditText mCartItemQuantity;
        public final ImageButton btnRemoveCartItem;
        public final TextView mDescTextView;
        public final TextView mCodeTextView;
        public final TextView mPricingUnitTextView;
        ImageButton btnIncrementCart, btnDecrementCart;

        private FirebaseAuth mAuth;
        private String userid;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCartItemImage = (ImageView) view.findViewById(R.id.cart_item_image);
            mCartItemTitle = (TextView) view.findViewById(R.id.cart_item_title);
            mCartItemTotal = (TextView) view.findViewById(R.id.cart_item_price);
            mCartItemQuantity = (EditText) view.findViewById(R.id.cart_item_quantity);
            btnIncrementCart = view.findViewById(R.id.btn_increment);
            btnDecrementCart = view.findViewById(R.id.btn_decrement);
            mDescTextView = view.findViewById(R.id.tv_cart_desc);
            mCodeTextView = view.findViewById(R.id.tv_cart_code);
            mPricingUnitTextView = view.findViewById(R.id.tv_pricing_unit);
            btnRemoveCartItem = (ImageButton) view.findViewById(R.id.remove_cart_item_btn);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCartItemTitle.getText() + "'";
        }
    }
}

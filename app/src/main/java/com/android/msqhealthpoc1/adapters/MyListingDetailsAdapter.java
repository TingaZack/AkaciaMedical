package com.android.msqhealthpoc1.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").child("cart-items").child(mValues.get(position).code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CartItem item = dataSnapshot.getValue(CartItem.class);
                            if (holder.mQuantity.getText() != null) {
                                item.setQuantity(item.getQuantity() + Integer.parseInt(holder.mQuantity.getText().toString()));
                            } else {
                                item.setQuantity(item.getQuantity() + 1);
                            }
                            Map<String, Object> postValues = item.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/cart/cart-items/" + mValues.get(position).getCode(), postValues);
                            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity, "Item added to cart", Toast.LENGTH_SHORT).show();
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
                            CartItem cartItem = new CartItem();
                            cartItem.setOwner_id(user.getUid());
                            cartItem.setProduct(holder.mItem);
                            cartItem.setQuantity(1);
                            if (holder.mQuantity.getText() != null) {
                                cartItem.setQuantity(Integer.parseInt(holder.mQuantity.getText().toString()));
                            } else {
                                cartItem.setQuantity(1);
                            }

                            Map<String, Object> postValues = cartItem.toMap();

                            String key = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").push().getKey();


                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/cart/cart-items/" + mValues.get(position).getCode(), postValues);

                            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity, "Item added to cart", Toast.LENGTH_SHORT).show();
                                    } else {
                                        task.getException().printStackTrace();
                                    }
                                }
                            });
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
        public final TextInputEditText mQuantity;
        public Product mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDescriptionView = (TextView) view.findViewById(R.id.info_description);
            mPricingUnitView = (TextView) view.findViewById(R.id.info_unit);
            mPricingView = (TextView) view.findViewById(R.id.info_price);
            btnAddToCart = (Button) view.findViewById(R.id.addToCart);
            mQuantity = (TextInputEditText) view.findViewById(R.id.cart_item_quantity);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionView.getText() + "'";
        }
    }
}

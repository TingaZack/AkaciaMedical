package com.android.msqhealthpoc1.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.model.CartItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private DataSnapshot dataSnapshot;

    public CartDialogListAdapter(List<CartItem> items, Activity activity, DataSnapshot dataSnapshot) {
        mValues = items;
        this.activity = activity;
        this.dataSnapshot = dataSnapshot;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        System.out.println(mValues.get(position).getProduct().consumables);


        DecimalFormat decimalDigitsFormat = new DecimalFormat("##.00");

        Glide.with(activity).load(mValues.get(position).getProduct().trueImageUrl).into(holder.mCartItemImage);
        holder.mCartItemTitle.setText(mValues.get(position).getProduct().consumables);
        holder.mCartItemQuantity.setText(String.valueOf(mValues.get(position).getQuantity()));
        holder.mCartItemTotal.setText(String.valueOf("R" + decimalDigitsFormat.format(mValues.get(position).getProduct().price * Integer.parseInt(holder.mCartItemQuantity.getText().toString()))));
        holder.btnRemoveCartItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Removing item", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cart").child("cart-items").child(mValues.get(position).getProduct().getCode()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity, "Item successfully removed", Toast.LENGTH_SHORT).show();
                                        notifyItemRemoved(position);
                                    } else {
                                        Toast.makeText(activity, "Cart item removal failed", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(activity, "Connection Error. Please check internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


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

                    Double amount = mValues.get(position).getProduct().price * Double.parseDouble(sb.toString());

                    holder.mCartItemTotal.setText("R" + decimalDigitsFormat.format(amount));

                    mValues.get(position).setQuantity(Integer.parseInt(sb.toString()));

                    Map<String, Object> postValues = mValues.get(position).toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/cart/cart-items/" + mValues.get(position).getProduct().code, postValues);
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(activity, holder.mCartItemTitle.getText().toString() + " quantity changed.", Toast.LENGTH_SHORT).show();
                            } else {
                                task.getException().printStackTrace();
                            }
                        }
                    });

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        holder.mCartItemQuantity.addTextChangedListener(watcher);
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
        public final TextInputEditText mCartItemQuantity;
        public final Button btnRemoveCartItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCartItemImage = (ImageView) view.findViewById(R.id.cart_item_image);
            mCartItemTitle = (TextView) view.findViewById(R.id.cart_item_title);
            mCartItemTotal = (TextView) view.findViewById(R.id.cart_item_price);
            mCartItemQuantity = (TextInputEditText) view.findViewById(R.id.cart_item_quantity);
            btnRemoveCartItem = (Button) view.findViewById(R.id.remove_cart_item_btn);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCartItemTitle.getText() + "'";
        }
    }
}

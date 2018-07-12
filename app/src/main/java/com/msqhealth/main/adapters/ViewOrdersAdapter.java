package com.msqhealth.main.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;
import com.msqhealth.main.model.Cart;
import com.msqhealth.main.model.CartItem;
import com.msqhealth.main.model.Product;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 26/03/2018.
 */

public class ViewOrdersAdapter extends RecyclerView.Adapter<ViewOrdersAdapter.ViewOrdersHolder> {

    private final List<Cart> mValues;
    private Activity activity;
    private DataSnapshot dataSnapshot;

    public ViewOrdersAdapter(List<Cart> items, Activity activity, DataSnapshot dataSnapshot) {
        this.mValues = items;
        this.activity = activity;
        this.dataSnapshot = dataSnapshot;
    }

    @Override
    public ViewOrdersHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_order_row, parent, false);
        return new ViewOrdersHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewOrdersHolder holder, int position) {

        holder.mItem = mValues.get(position);

        System.out.println("Consumables: " + mValues.get(position).getProduct().getConsumables());

        holder.mProductNameTextView.setText(mValues.get(position).getProduct().getConsumables());
        holder.mProductDescTextView.setText(mValues.get(position).getProduct().getDescription());
        holder.mProductCodeTextView.setText(mValues.get(position).getProduct().getCode());
        if (mValues.get(position).getProduct().getUnit_of_messuremeant().equals("100") || mValues.get(position).getProduct().getUnit_of_messuremeant().equals("200")
                || mValues.get(position).getProduct().getUnit_of_messuremeant().equals("50") || mValues.get(position).getProduct().getUnit_of_messuremeant().equals("10")) {
            holder.mProductUnitTextView.setText("per " + mValues.get(position).getProduct().getUnit_of_messuremeant() + "'s");
        } else {
            holder.mProductUnitTextView.setText(mValues.get(position).getProduct().getUnit_of_messuremeant());
        }
        holder.mProductPriceTextView.setText("R" + String.valueOf(mValues.get(position).getProduct().getPrice()));
        holder.mProductQuantityTextView.setText(String.valueOf(mValues.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewOrdersHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mProductNameTextView;
        public final TextView mProductDescTextView;
        public final TextView mProductPriceTextView;
        public final TextView mProductUnitTextView;
        public final TextView mProductCodeTextView;
        public final TextView mProductQuantityTextView;
        public final TextView mProductDateTextView;

        public Cart mItem;

        public ViewOrdersHolder(View view) {
            super(view);
            mView = view;

            mProductNameTextView = mView.findViewById(R.id.tv_product_name);
            mProductDescTextView = mView.findViewById(R.id.tv_pro_desc);
            mProductPriceTextView = mView.findViewById(R.id.tv_pro_price);
            mProductUnitTextView = mView.findViewById(R.id.tv_per_unit);
            mProductCodeTextView = mView.findViewById(R.id.tv_pro_code);
            mProductQuantityTextView = mView.findViewById(R.id.tv_pro_quantity);
            mProductDateTextView = mView.findViewById(R.id.tv_pro_quantity);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mProductNameTextView.getText() + "'";
        }


    }


}

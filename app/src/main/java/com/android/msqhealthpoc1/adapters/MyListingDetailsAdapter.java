package com.android.msqhealthpoc1.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.model.Product;

import java.util.List;


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
    public void onBindViewHolder(final ViewHolder holder, int position) {

        System.out.println(mValues.get(position).description);
        holder.mItem = mValues.get(position);
        holder.mDescriptionView.setText(mValues.get(position).description);
        holder.mPricingUnitView.setText(mValues.get(position).unit_of_messuremeant);
        holder.mPricingView.setText(String.valueOf("R" + mValues.get(position).price));

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
        public Product mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDescriptionView = (TextView) view.findViewById(R.id.info_description);
            mPricingUnitView = (TextView) view.findViewById(R.id.info_unit);
            mPricingView = (TextView) view.findViewById(R.id.info_price);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionView.getText() + "'";
        }
    }
}

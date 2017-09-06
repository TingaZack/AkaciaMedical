package com.android.msqhealthpoc1.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.ListingDetailsActivity;
import com.android.msqhealthpoc1.model.Product;

import java.util.List;

public class MyProductListRecyclerViewAdapter extends RecyclerView.Adapter<MyProductListRecyclerViewAdapter.ViewHolder> {

    private final List<Product> mValues;
    private Activity activity;

    public MyProductListRecyclerViewAdapter(List<Product> items, Activity activity) {
        this.mValues = items;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category_product_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).consumables);
        holder.mContentView.setText(String.valueOf(mValues.get(position).price));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ListingDetailsActivity.class);
                intent.putExtra("title", mValues.get(holder.getAdapterPosition()).consumables);
                intent.putExtra("image_url", mValues.get(holder.getAdapterPosition()).trueImageUrl);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Product mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

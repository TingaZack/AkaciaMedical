package com.msqhealth.main.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.msqhealth.main.R;
import com.msqhealth.main.activities.ListingDetailsActivity;
import com.msqhealth.main.model.Category;
import com.bumptech.glide.Glide;

import java.util.List;

public class MyProductRecyclerViewAdapter extends RecyclerView.Adapter<MyProductRecyclerViewAdapter.ViewHolder> {

    private final List<Category> mValues;
    private Activity activity;

    public MyProductRecyclerViewAdapter(List<Category> items, Activity activity) {
        mValues = items;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mContentView.setText(mValues.get(position).title);
        Glide.with(activity).load(mValues.get(position).image_url).into(holder.mProductImage);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ListingDetailsActivity.class);
                intent.putExtra("title", mValues.get(position).title);
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
        public final ImageView mProductImage;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProductImage = (ImageView) view.findViewById(R.id.product_image);
            mContentView = (TextView) view.findViewById(R.id.product_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

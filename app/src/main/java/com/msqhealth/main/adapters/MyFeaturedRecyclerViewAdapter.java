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
import com.msqhealth.main.activities.FeaturedItemDetails;
import com.msqhealth.main.model.FeaturedItem;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

public class MyFeaturedRecyclerViewAdapter extends RecyclerView.Adapter<MyFeaturedRecyclerViewAdapter.ViewHolder> {

    private final List<FeaturedItem> mValues;
    private final List<String> mPlaces;
    Activity activity;

    public MyFeaturedRecyclerViewAdapter(List<FeaturedItem> items, Activity activity, List<String> places) {
        mValues = items;
        mPlaces = places;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_featured, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Glide.with(activity).load(mValues.get(position).image_url).into(holder.mIdView);
        System.out.println(mValues.get(position).image_url);
        holder.mContentView.setText(mValues.get(position).title);
        holder.mDescriptionView.setText(mPlaces.get(position));

        Random rand = new Random();
        int n = rand.nextInt(50) + 1;

        holder.mTimeView.setText(n + " days left");

        holder.mIdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, FeaturedItemDetails.class);
                intent.putExtra("title", mValues.get(holder.getAdapterPosition()).title);
                intent.putExtra("image_url", mValues.get(holder.getAdapterPosition()).image_url);
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
        public final ImageView mIdView;
        public final TextView mContentView;
        public final TextView mDescriptionView;
        public final TextView mTimeView;
        public FeaturedItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (ImageView) view.findViewById(R.id.featured_image);
            mContentView = (TextView) view.findViewById(R.id.title);
            mDescriptionView = (TextView) view.findViewById(R.id.place);
            mTimeView = (TextView) view.findViewById(R.id.days_left);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

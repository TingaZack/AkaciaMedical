package com.msqhealth.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.msqhealth.main.R;
import com.bumptech.glide.Glide;

public class FeaturedItemDetails extends AppCompatActivity {


    Intent intent;
    String imageUrl, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        intent = getIntent();
        intent.getExtras();

        title = intent.getStringExtra("title");
        imageUrl = intent.getStringExtra("image_url");


        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        //((TextView) findViewById(R.id.product_description)).setText(_object.getKeyBenefitsDetails());
        Glide.with(FeaturedItemDetails.this).load(imageUrl).into(((ImageView) findViewById(R.id.image)));
        collapsingToolbarLayout.setScrimsShown(true);

    }
}

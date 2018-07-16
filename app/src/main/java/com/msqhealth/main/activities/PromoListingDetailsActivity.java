package com.msqhealth.main.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.msqhealth.main.R;
import com.msqhealth.main.adapters.MyListingDetailsAdapter;
import com.msqhealth.main.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PromoListingDetailsActivity extends AppCompatActivity {

    Intent intent;
    String imageUrl, title;

    MyListingDetailsAdapter adapter;


    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private DatabaseReference mDatabase;
    List<Product> productList;

    ProgressDialog pDialog;

    RecyclerView recyclerView;

    Query mQueryPromoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        if (isNetworkAvailable()) {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

            intent = getIntent();
            intent.getExtras();


            pDialog = new ProgressDialog(PromoListingDetailsActivity.this);
            pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pDialog.setMessage("Just a second...");
            pDialog.show();

            title = intent.getStringExtra("title");

            collapsingToolbarLayout.setTitle(title);
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
            collapsingToolbarLayout.setScrimsShown(true);


            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.keepSynced(true);

            mQueryPromoContent = mDatabase.child("products").orderByChild("PROMO").equalTo(true);

            productList = new ArrayList<>();

            adapter = new MyListingDetailsAdapter(productList, this);

            recyclerView = (RecyclerView) findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adapter);


            mQueryPromoContent.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {

                    System.out.println("CHILD COUNT CH---: " + snapshot.getValue());

                    if(snapshot.hasChildren()){

                    }
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        System.out.println("CHILD COUNT: " + snapshot.getChildrenCount());
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.getMessage());
                }
            });

        } else if (!isNetworkAvailable()) {

            Snackbar snack = Snackbar.make(findViewById(R.id.relative_layout), "No Connection Available, please check your internet settings and try again.", Snackbar.LENGTH_INDEFINITE).setDuration(10000);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


package com.msqhealth.main.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ImageView;

import com.msqhealth.main.R;
import com.msqhealth.main.adapters.MyListingDetailsAdapter;
import com.msqhealth.main.model.Product;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListingDetailsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        intent = getIntent();
        intent.getExtras();


        pDialog = new ProgressDialog(ListingDetailsActivity.this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Just a second...");
        pDialog.show();

        title = intent.getStringExtra("title");

        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        collapsingToolbarLayout.setScrimsShown(true);

        productList = new ArrayList<>();

        adapter = new MyListingDetailsAdapter(productList, this);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

            mDatabase = FirebaseDatabase.getInstance().getReference();


            mDatabase.child("products").addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    productList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (postSnapshot.child("CONSUMABLES").getValue().toString().equals(title)) {
                            Product products = new Product(((String) postSnapshot.child("CODE").getValue()),
                                    (String) postSnapshot.child("CONSUMABLES").getValue(),
                                    (String) postSnapshot.child("DESCRIPTION").getValue(),
                                    Double.parseDouble(postSnapshot.child("PRICING").getValue().toString().replace(",", ".")),
                                    postSnapshot.child("PRICING_UNIT").getValue().toString(),
                                    (String) postSnapshot.child("True Image").getValue());
                            productList.add(products);
                        }
                        if (productList.size() > 0)
                            Glide.with(ListingDetailsActivity.this).load(productList.get(0).trueImageUrl).into(((ImageView) findViewById(R.id.image)));
                    }
                    pDialog.dismiss();
                    adapter.notifyDataSetChanged();
                    System.out.println(adapter.getItemCount() + " in adapter");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.getMessage());
                }
            });
    }

}


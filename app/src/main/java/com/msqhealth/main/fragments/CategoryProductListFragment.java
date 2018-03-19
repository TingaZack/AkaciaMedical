package com.msqhealth.main.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import com.msqhealth.main.R;
import com.msqhealth.main.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductListFragment extends Activity {

    // TODO: Customize parameters
    private int mColumnCount = 1;


    private DatabaseReference mDatabase;
    private List<Product> productList;

    private String name;

    Intent intent;

    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_category_product_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        pDialog = new ProgressDialog(this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Just a second...");
        pDialog.show();

        intent = getIntent();
        intent.getExtras();

        name = intent.getStringExtra("name");

        productList = new ArrayList<>();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }

        Query query = mDatabase.child("products").equalTo("CONSUMABLES", name);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("products").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product category = new Product(((String) postSnapshot.child("CODE").getValue()), (String) postSnapshot.child("CONSUMABLES").getValue(), (String) postSnapshot.child("DESCRIPTION").getValue(), (double) postSnapshot.child("PRICING").getValue(), postSnapshot.child("PRICING UNIT").getValue().toString(), (String) postSnapshot.child("True Image").getValue());
                    productList.add(category);
                }
                pDialog.dismiss();
                recyclerView.setAdapter(new MyProductListRecyclerViewAdapter(productList, CategoryProductListFragment.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }
}

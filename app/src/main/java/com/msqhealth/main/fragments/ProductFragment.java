package com.msqhealth.main.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.msqhealth.main.R;
import com.msqhealth.main.adapters.MyProductRecyclerViewAdapter;
import com.msqhealth.main.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 3;


    private DatabaseReference mDatabase;
    private List<Category> categoryList;

    private ProgressDialog pDialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Just a second...");
        pDialog.show();

        categoryList = new ArrayList<>();
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            mDatabase.child("categories").addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    categoryList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Category category = new Category(((String) postSnapshot.child("Category").getValue()), (String) postSnapshot.child("image").getValue());
                        categoryList.add(category);
                    }
                    pDialog.dismiss();
                    recyclerView.setAdapter(new MyProductRecyclerViewAdapter(categoryList, getActivity()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.getMessage());
                }
            });
        }
        return view;
    }

}

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
import com.msqhealth.main.adapters.MyFeaturedRecyclerViewAdapter;
import com.msqhealth.main.model.FeaturedItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeaturedFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    List<FeaturedItem> featuredItemsList;
    List<String> places;
    ProgressDialog pDialog;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private DatabaseReference mDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeaturedFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FeaturedFragment newInstance(int columnCount) {
        FeaturedFragment fragment = new FeaturedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_featured_list, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        featuredItemsList = new ArrayList<>();
        places = new ArrayList<>();

        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Just a second...");
        pDialog.show();



        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


            mDatabase.child("promotional content").addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    featuredItemsList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        FeaturedItem featuredItem = new FeaturedItem(((String) postSnapshot.child("TITLE").getValue()), ((String) postSnapshot.child("IMAGE").getValue()));
                        featuredItemsList.add(featuredItem);
                        places.add(postSnapshot.child("DESCRIPTION").getValue().toString());
                    }
                    pDialog.dismiss();

                    recyclerView.setAdapter(new MyFeaturedRecyclerViewAdapter(featuredItemsList, getActivity(), places));

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

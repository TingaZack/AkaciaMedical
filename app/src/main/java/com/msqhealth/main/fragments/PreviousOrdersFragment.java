package com.msqhealth.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;

/**
 * Created by Admin on 19/03/2018.
 */

public class PreviousOrdersFragment extends Fragment {

    private DatabaseReference mDatabase;
    private Query mQuery;
    private FirebaseAuth mAuth;
    private String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.previous_orders_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("deliveries");
            mQuery = mDatabase.child("completed").orderByChild("userID").equalTo(user_id);
            mQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("CHILDREN: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        System.out.println("ASSIGN: " + snapshot.child("assign").getValue());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        return rootView;
    }
}

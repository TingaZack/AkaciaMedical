package com.msqhealth.main.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;
import com.msqhealth.main.adapters.MyProductRecyclerViewAdapter;
import com.msqhealth.main.adapters.Pending_History_OrdersAdapter;
import com.msqhealth.main.model.Cart;
import com.msqhealth.main.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 19/03/2018.
 */

public class PendingOrdersFragment extends Fragment {

    private DatabaseReference mDatabase;
    private Query mQuery;
    private FirebaseAuth mAuth;
    private String user_id;

    private int mColumnCount = 1;

    private List<Cart> cartList;

    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.global_list, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Just a second...");
        pDialog.show();

        cartList = new ArrayList<>();
        // Set the adapter
        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            final RecyclerView recyclerView = (RecyclerView) rootView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

//            mDatabase = FirebaseDatabase.getInstance().getReference().child("deliveries");

            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                user_id = mAuth.getCurrentUser().getUid();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("deliveries").child("pending");
//                mQuery = mDatabase.child(user_id).orderByChild("userID").equalTo(user_id);

                mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cartList.clear();
                        System.out.println("CHILDREN: " + dataSnapshot.getChildrenCount());
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            System.out.println("ASSIGN: " + snapshot.child("assign").getValue());
                            Cart cart = new Cart(((long) snapshot.child("timeStamp").getValue()),
                                    ((String) snapshot.child("invoice_number").getValue()),
                                    ((double) snapshot.child("subtotal").getValue()));
                            cartList.add(cart);
                        }
                        pDialog.dismiss();
                        recyclerView.setAdapter(new Pending_History_OrdersAdapter(cartList, getActivity()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError.getMessage());
                    }
                });

            }
        }

        return rootView;
    }
}

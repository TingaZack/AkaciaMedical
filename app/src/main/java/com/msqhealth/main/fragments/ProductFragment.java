package com.msqhealth.main.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.msqhealth.main.R;
import com.msqhealth.main.adapters.MyProductRecyclerViewAdapter;
import com.msqhealth.main.helpers.PrefManager;
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
    private Activity activity;
    private PrefManager prefManager;
    private String dr_name;

    private FirebaseUser mUser;

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
        prefManager = new PrefManager(getContext());

        dr_name = getActivity().getIntent().getStringExtra("dr_name");

        mUser = FirebaseAuth.getInstance().getCurrentUser();

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
                    System.out.println(" CHI<D~: " + snapshot.getChildrenCount());
                    if (!snapshot.exists()) {
//                        mPromoTextView.setVisibility(View.VISIBLE);
                        System.out.println("HELLO COUNT");
                    } else {
                        System.out.println("BYE COUNT");
                    }
                    categoryList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Category category = new Category(((String) postSnapshot.child("Category").getValue()), (String) postSnapshot.child("image").getValue());
                        categoryList.add(category);
                    }
                    recyclerView.setAdapter(new MyProductRecyclerViewAdapter(categoryList, getActivity()));
                    pDialog.dismiss();
                    checkUserRegistration();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.getMessage());
                }
            });
        }
        return view;
    }

    /*
     * Check whether the user was registering or not( the state should be true or false). If the state is true, the user will
     * see a popup welcoming them to the app here on MainActivity and if it's false, the will never see that welcome pop-up
     * until they register again.
     * */
    public void checkUserRegistration() {
        if (!prefManager.isFirstTimeRegister()) {
            System.out.println("YES 1: " + prefManager.isFirstTimeRegister());
            showWelcomeDialog();
        }
    }

    public void showWelcomeDialog() {
        final View mView = LayoutInflater.from(getContext()).inflate(R.layout.welcome_dialog, null);

        final Button mWelcomeDone = mView.findViewById(R.id.btn_welcome_done);
        final TextView mWelcomeTextView = mView.findViewById(R.id.tv_welcome);

        final android.support.v7.app.AlertDialog.Builder aBuilder = new android.support.v7.app.AlertDialog.Builder(getContext(), R.style.CustomDialog);
        aBuilder.setView(mView);
        aBuilder.setCancelable(false);

        final android.support.v7.app.AlertDialog alert = aBuilder.create();

        FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("CECK NAME" + dataSnapshot.child("Name").getValue());

                if (TextUtils.isEmpty(dr_name)) {
                    mWelcomeTextView.setText("Welcome " + dataSnapshot.child("Name").getValue());
                } else {
                    mWelcomeTextView.setText("Welcome " + dr_name);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mWelcomeDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                prefManager.setToFirstTimeRegister(true);
                System.out.println("YES 2: " + prefManager.isFirstTimeRegister());
            }
        });

        alert.show();
    }

}

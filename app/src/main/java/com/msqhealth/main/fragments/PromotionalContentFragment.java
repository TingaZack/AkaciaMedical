package com.msqhealth.main.fragments;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;
import com.msqhealth.main.adapters.PromotionalContentRecyclerViewAdapter;
import com.msqhealth.main.model.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 17/02/2018.
 */

public class PromotionalContentFragment extends Fragment {
    private DatabaseReference mPromotionsDatabase;
    private RecyclerView mRecyclerView;

    // TODO: Customize parameters
    private int mColumnCount = 1;


    private DatabaseReference mDatabase, mDebtorDatabaseReference;
    private ValueEventListener mValueEventListener;
    private List<Product> productList;

    private ProgressDialog pDialog;
    private Query mQueryCurrentUser;
    private FirebaseUser mUser;

    private TextView mPromoTextView, mNoPromoTextView;
    private ImageView mNoPromoAvailableImageView;
    private LinearLayout mNoPromoLinearLayout, mNoUserLinearLayout, mNoDebtorCodeLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PromotionalContentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.global_list, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Just a second...");
        pDialog.show();

        mNoUserLinearLayout = rootView.findViewById(R.id.no_user_linear);

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        mPromoTextView = rootView.findViewById(R.id.promo_textview);
        mNoPromoTextView = rootView.findViewById(R.id.promo_textview_not_available);
        mNoPromoAvailableImageView = rootView.findViewById(R.id.no_promo_imageview);

        mNoPromoLinearLayout = rootView.findViewById(R.id.no_promo_linear);
        mNoDebtorCodeLayout = rootView.findViewById(R.id.not_verified_linear);


        mRecyclerView = rootView.findViewById(R.id.promotion_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mUser != null) {

            mPromotionsDatabase = FirebaseDatabase.getInstance().getReference().child("products");
            mPromotionsDatabase.keepSynced(true);

            mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
            mDatabase.keepSynced(true);

            mDebtorDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
            mDebtorDatabaseReference.keepSynced(true);


            productList = new ArrayList<>();

            mValueEventListener = mDebtorDatabaseReference.child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    System.out.println("What happeed?" + dataSnapshot.getValue());

                    if (dataSnapshot.hasChild("debtorCode")) {

                        mNoDebtorCodeLayout.setVisibility(View.GONE);

                        Query query = mDatabase.orderByChild("PROMO").equalTo(true);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                System.out.println("CHI<D~: " + dataSnapshot.getChildrenCount());
                                if (!dataSnapshot.exists()) {
                                    mNoPromoLinearLayout.setVisibility(View.VISIBLE);
                                } else {
                                    mNoPromoLinearLayout.setVisibility(View.GONE);
                                }

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    System.out.println("Count: " + snapshot.getChildrenCount());
                                    System.out.println("PROMO VALUE: " + snapshot.child("PROMO").getValue());

                                    String percentage = (String) snapshot.child("PERCENTAGE").getValue();
                                    String pricing = (String) snapshot.child("PRICING").getValue();
                                    double final_price = (Double.parseDouble(pricing) - (Double.parseDouble(percentage) / 100) * Double.parseDouble(pricing));
                                    DecimalFormat df = new DecimalFormat("##.00");

                                    System.out.println("FINAL PRICE: " + df.format(final_price));

                                    Product product = new Product(((String) snapshot.child("CODE").getValue()),
                                            ((String) snapshot.child("CONSUMABLES").getValue()),
                                            (String) snapshot.child("DESCRIPTION").getValue(),
                                            Double.parseDouble(df.format(final_price)),
                                            (String) snapshot.child("PRICING_UNIT").getValue(),
                                            percentage, (String) snapshot.child("True Image").getValue(),
                                            (boolean) snapshot.child("PROMO").getValue(),
                                            (String) snapshot.child("END").getValue(),
                                            (String) snapshot.child("START").getValue());
                                    productList.add(product);
                                }
                                pDialog.dismiss();
                                mRecyclerView.setAdapter(new PromotionalContentRecyclerViewAdapter(productList, getActivity()));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                pDialog.dismiss();
                                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        pDialog.dismiss();
                        mNoDebtorCodeLayout.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    pDialog.dismiss();
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            pDialog.dismiss();
            mNoUserLinearLayout.setVisibility(View.VISIBLE);
        }


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        pDialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        pDialog.dismiss();
//        if (mDebtorDatabaseReference != null) {
//            mDebtorDatabaseReference.removeEventListener(mValueEventListener);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
//        if (mDebtorDatabaseReference != null) {
//            mDebtorDatabaseReference.removeEventListener(mValueEventListener);
//        }
    }
}


//package com.msqhealth.main.fragments;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.msqhealth.main.R;
//import com.msqhealth.main.adapters.PromotionalContentRecyclerViewAdapter;
//import com.msqhealth.main.model.Product;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Admin on 17/02/2018.
// */
//
//public class PromotionalContentFragment extends Fragment {
//
//    private DatabaseReference mPromotionsDatabase;
//    private RecyclerView mRecyclerView;
//
//    // TODO: Customize parameters
//    private int mColumnCount = 1;
//
//
//    private DatabaseReference mDatabase;
//    private List<Product> productList;
//
//    private ProgressDialog pDialog;
//    private Query mQueryCurrentUser;
//    private FirebaseUser mUser;
//
//    private TextView mPromoTextView, mNoPromoTextView;
//    private ImageView mNoPromoAvailableImageView;
//    private LinearLayout mNoPromoLinearLayout, mNoUserLinearLayout, mNotVerifiedLinearLayout;
//
//    /**
//     * Mandatory empty constructor for the fragment manager to instantiate the
//     * fragment (e.g. upon screen orientation changes).
//     */
//    public PromotionalContentFragment() {
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final View rootView = inflater.inflate(R.layout.global_list, container, false);
//
//        pDialog = new ProgressDialog(getActivity());
//        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        pDialog.setMessage("Just a second...");
//        pDialog.show();
//
//        mNoUserLinearLayout = rootView.findViewById(R.id.no_user_linear);
//        mNotVerifiedLinearLayout = rootView.findViewById(R.id.not_verified_linear);
//
//        mPromoTextView = rootView.findViewById(R.id.promo_textview);
//        mNoPromoTextView = rootView.findViewById(R.id.promo_textview_not_available);
//        mNoPromoAvailableImageView = rootView.findViewById(R.id.no_promo_imageview);
//
//        mNoPromoLinearLayout = rootView.findViewById(R.id.no_promo_linear);
//
//        mRecyclerView = rootView.findViewById(R.id.promotion_list);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        productList = new ArrayList<>();
//
//        mUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (mUser != null) {
//
//            mPromotionsDatabase = FirebaseDatabase.getInstance().getReference().child("products");
//            mPromotionsDatabase.keepSynced(true);
//
//            mDatabase = FirebaseDatabase.getInstance().getReference().child("products");
//            mDatabase.keepSynced(true);
//
//            Query query = mDatabase.orderByChild("PROMO").equalTo(true);
//            query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    System.out.println("CHI<D~: " + dataSnapshot.getChildrenCount());
//                    if (!dataSnapshot.exists()) {
//                        mNoPromoLinearLayout.setVisibility(View.VISIBLE);
//                    } else {
//                        mNoPromoLinearLayout.setVisibility(View.GONE);
//                    }
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        System.out.println("Count: " + snapshot.getChildrenCount());
//                        System.out.println("PROMO VALUE: " + snapshot.child("PROMO").getValue());
//
//                        String percentage = (String) snapshot.child("PERCENTAGE").getValue();
//                        String pricing = (String) snapshot.child("PRICING").getValue();
//                        double final_price = (Double.parseDouble(pricing) - (Double.parseDouble(percentage) / 100) * Double.parseDouble(pricing));
//                        DecimalFormat df = new DecimalFormat("##.00");
//
//                        System.out.println("FINAL PRICE: " + df.format(final_price));
//
//                        Product product = new Product(((String) snapshot.child("CODE").getValue()),
//                                ((String) snapshot.child("CONSUMABLES").getValue()),
//                                (String) snapshot.child("DESCRIPTION").getValue(),
//                                Double.parseDouble(df.format(final_price)),
//                                (String) snapshot.child("PRICING_UNIT").getValue(),
//                                percentage, (String) snapshot.child("True Image").getValue(),
//                                (boolean) snapshot.child("PROMO").getValue(), (String) snapshot.child("END").getValue());
//                        productList.add(product);
//                    }
//                    pDialog.dismiss();
//                    mRecyclerView.setAdapter(new PromotionalContentRecyclerViewAdapter(productList, getActivity()));
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        } else {
//            pDialog.dismiss();
//            mNoUserLinearLayout.setVisibility(View.VISIBLE);
//        }
//
//
//        return rootView;
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        pDialog.dismiss();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        pDialog.dismiss();
//    }
//}

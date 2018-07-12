package com.msqhealth.main.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;
import com.msqhealth.main.model.Product;

public class ViewOrdersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String user_id, invoiceIntent;
    private String product_name;

    private RecyclerView mRecyclerView;
    private DataSnapshot cartnapshot;

    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_list);

        invoiceIntent = getIntent().getStringExtra("invoice_number");
        Toast.makeText(this, "IN" + invoiceIntent, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("deliveries").child("pending").child(user_id);

            mQuery = mDatabase.orderByChild("invoice_number").equalTo(invoiceIntent);
            mQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        System.out.println("SNAP: " + snapshot.child("assign").getValue());

                        cartnapshot = snapshot.child("cart-items");
                        System.out.println("SHOT: " + cartnapshot.getValue());

//                            if(snapshot.child("cart-items").hasChildren()){
//                                for(DataSnapshot data: contactSnapshot.getChildren()){
//                                    if(data.child("product").hasChildren()) {
//                                        System.out.println("Qua: " + data.child("code").getValue());
//                                        for (DataSnapshot shot : data.getChildren()) {
//                                            System.out.println("Qua: " + shot.child("code").getValue());
//                                        }
//                                    }
//                                }
//                            }
                    }

                    for (DataSnapshot snap : cartnapshot.getChildren()) {

                        System.out.println("SHOT Activity: " + snap.child("product").child("code").getValue());

                        product_name = (String) snap.child("product").child("consumables").getValue();

//                        viewHolder.setProductName((String) snap.child("product").child("consumables").getValue());
//                        viewHolder.setProductDesc((String) snap.child("product").child("description").getValue());
//                        viewHolder.setProductCode((String) snap.child("product").child("code").getValue());

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mRecyclerView = findViewById(R.id.promotion_list);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Product, OrdersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, OrdersViewHolder>(
                Product.class,
                R.layout.view_order_row,
                OrdersViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(final OrdersViewHolder viewHolder, final Product model, int position) {

                for (DataSnapshot snap : cartnapshot.getChildren()) {

                    System.out.println("SHOT Activity 2: " + snap.child("product").child("code").getValue());

                    product_name = (String) snap.child("product").child("consumables").getValue();
                    viewHolder.setProductName((String) snap.child("product").child("consumables").getValue());

//                        viewHolder.setProductName((String) snap.child("product").child("consumables").getValue());
//                        viewHolder.setProductDesc((String) snap.child("product").child("description").getValue());
//                        viewHolder.setProductCode((String) snap.child("product").child("code").getValue());

                }

//                Query mQuery = mDatabase.orderByChild("invoice_number").equalTo(invoiceIntent);
//                mQuery.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                            System.out.println("SNAP: " + snapshot.child("assign").getValue());
//
//                            cartnapshot = snapshot.child("cart-items");
//                            System.out.println("SHOT: " + cartnapshot.getValue());
//
////                            if(snapshot.child("cart-items").hasChildren()){
////                                for(DataSnapshot data: contactSnapshot.getChildren()){
////                                    if(data.child("product").hasChildren()) {
////                                        System.out.println("Qua: " + data.child("code").getValue());
////                                        for (DataSnapshot shot : data.getChildren()) {
////                                            System.out.println("Qua: " + shot.child("code").getValue());
////                                        }
////                                    }
////                                }
////                            }
//                        }
//
//                        for (DataSnapshot snap : cartnapshot.getChildren()) {
//
//                            System.out.println("SHOT: " + snap.child("product").child("code").getValue());
//
//                            viewHolder.setProductName((String) snap.child("product").child("consumables").getValue());
//                            viewHolder.setProductDesc((String) snap.child("product").child("description").getValue());
//                            viewHolder.setProductCode((String) snap.child("product").child("code").getValue());
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProductName(String name){
            TextView nameTextView = mView.findViewById(R.id.tv_product_name);
            nameTextView.setText(name);
        }

        public void setProductDesc(String desc){
            TextView descTextView = mView.findViewById(R.id.tv_pro_desc);
            descTextView.setText(desc);
        }
        public void setProductUnit(String unit){
            TextView unitTextView = mView.findViewById(R.id.tv_per_unit);
            unitTextView.setText(unit);
        }
        public void setProductCode(String code){
            TextView codeTextView = mView.findViewById(R.id.tv_pro_code);
            codeTextView.setText(code);
        }
        public void setProductDateTime(String dateTime){
            TextView dateTimeTextView = mView.findViewById(R.id.tv_pro_date_time);
            dateTimeTextView.setText(dateTime);
        }

        public void setProductQuantity(int quantity){
            TextView quantityTextView = mView.findViewById(R.id.tv_pro_quantity);
            quantityTextView.setText(String.valueOf(quantity));
        }

    }
}

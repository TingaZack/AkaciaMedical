package com.msqhealth.main.activities.orders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.R;
import com.msqhealth.main.activities.ListingDetailsActivity;
import com.msqhealth.main.adapters.MyListingDetailsAdapter;
import com.msqhealth.main.adapters.ViewOrdersAdapter;
import com.msqhealth.main.model.Cart;
import com.msqhealth.main.model.CartItem;
import com.msqhealth.main.model.Product;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityOrdersView extends AppCompatActivity {

    ViewOrdersAdapter adapter;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Query mQuery;
    private Activity activity;

    private List<Cart> cartList;

    private ProgressDialog pDialog;

    private RecyclerView recyclerView;
    private String invoiceIntent, user_id;

    private DataSnapshot cartnapshot;

    private boolean state = true;

    private TextView mDateTextView, mInvoiceNumber, mTotalPriceTextView, mProcessedTextView, mPickedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_view);

        invoiceIntent = getIntent().getStringExtra("invoice_number");

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();

            cartList = new ArrayList<>();

            recyclerView = (RecyclerView) findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            mDateTextView = findViewById(R.id.tv_date);
            mInvoiceNumber = findViewById(R.id.tv_invoice);
            mTotalPriceTextView = findViewById(R.id.tv_pro_total_price);
            mProcessedTextView = findViewById(R.id.tv_processed);
            mPickedTextView = findViewById(R.id.tv_picked);

            mDatabase = FirebaseDatabase.getInstance().getReference().child("deliveries").child("pending").child(user_id);
            mQuery = mDatabase.orderByChild("invoice_number").equalTo(invoiceIntent);

            mQuery.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cartList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        cartnapshot = snapshot.child("cart-items");

                        long timeStamp = (long) snapshot.child("timeStamp").getValue();
                        double sub_total = (double) snapshot.child("subtotal").getValue();
                        state = (boolean) snapshot.child("assign").getValue();
                        System.out.println("STATE: " + state);

                        if (!state){
                            mProcessedTextView.setBackgroundResource(R.drawable.round_textview_bg);
                            mProcessedTextView.setTextColor(getResources().getColor(android.R.color.white));

                            mPickedTextView.setBackgroundResource(android.R.color.transparent);
                            mPickedTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        } else if (state){
                            mPickedTextView.setBackgroundResource(R.drawable.round_textview_bg);
                            mPickedTextView.setTextColor(getResources().getColor(android.R.color.white));

                            mProcessedTextView.setBackgroundResource(android.R.color.transparent);
                            mProcessedTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        }

                        Calendar cal = Calendar.getInstance(Locale.getDefault());
                        cal.setTimeInMillis(timeStamp);
                        String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();

                        mInvoiceNumber.setText(String.valueOf(snapshot.child("invoice_number").getValue()));
                        mDateTextView.setText(String.valueOf(date));

                        DecimalFormat df = new DecimalFormat("##.##");
                        mTotalPriceTextView.setText("R" + String.valueOf(df.format(sub_total)).replace(",", "."));

                        System.out.println("SHOT: " + cartnapshot.getValue());
                    }

                    for (DataSnapshot snap : cartnapshot.getChildren()) {

                        System.out.println("SHOT Activity: " + snap.child("product").child("code").getValue());

//                        Cart cart = new Cart(((String) snap.child("product").child("code").getValue()));

                        try {

                            Cart item = new Cart();
                            System.out.println("QUANTITY: " + snap.child("quantity").getValue());
                            item.setQuantity(Integer.parseInt(String.valueOf((long) snap.child("quantity").getValue())));
//                        item.setUserID(uid);


                            //Create and set product object
                            Product product = new Product();
                            product.setCode(snap.child("product").child("code").getValue().toString());
                            product.setConsumables(snap.child("product").child("consumables").getValue().toString());
                            product.setDescription(snap.child("product").child("description").getValue().toString());
                            product.setPrice(Double.parseDouble(snap.child("product").child("price").getValue().toString()));
                            product.setTrueImageUrl(snap.child("product").child("trueImageUrl").getValue().toString());
                            product.setUnit_of_messuremeant(snap.child("product").child("unit_of_messuremeant").getValue().toString());

                            item.setProduct(product);
                            if (item != null) {
                                cartList.add(item);
                            } else {
                                System.out.println("No Item found");
                            }
                        }catch (Exception e){
                            e.getMessage();
                        }

                        adapter = new ViewOrdersAdapter(cartList, activity, cartnapshot);
                        recyclerView.setAdapter(adapter);
                }
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
}

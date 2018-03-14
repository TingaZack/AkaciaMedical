package com.android.msqhealthpoc1.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.dialogs.CartDialogs;
import com.android.msqhealthpoc1.fragments.ProductFragment;
import com.android.msqhealthpoc1.fragments.PromotionalContentFragment;
import com.android.msqhealthpoc1.helpers.PrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    List<Map<String, Object>> items = new ArrayList<>();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private PrefManager prefManager;
    private DatabaseReference mDatabase, mDatabaseUsers;
    private FirebaseAuth mAuth;
    private int cart_count = 0;
    private int count_cart = 0;
    private TextView mCartCountTextView;
    private FirebaseUser user;

    private RelativeLayout mCartRelativeLayout;

    ImageButton btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_menu);

        //Initialising and setting my toolBar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        myToolbar.setTitle("MSQ Health");
        setSupportActionBar(myToolbar);
        //Setting up the back button click listener
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnCart = findViewById(R.id.cart);
        //Setting up the icon for the drop down menu, which is called options menu.
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_more_vert_black_24dp);
        myToolbar.setOverflowIcon(drawable);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");

        mCartCountTextView = findViewById(R.id.actionbar_notifcation_textview);
        mCartRelativeLayout = findViewById(R.id.cart_layout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        if (user != null) {
            mDatabaseUsers.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cart_count = (int) dataSnapshot.child("cart").child("cart-items").getChildrenCount();
                        if (cart_count == 0) {
                            mCartCountTextView.setVisibility(View.GONE);
                        } else {
                            mCartCountTextView.setVisibility(View.VISIBLE);
                            mCartCountTextView.setText(String.valueOf(cart_count));
                            btnCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CartDialogs dialog = new CartDialogs();
                                    dialog.show(getSupportFragmentManager(), "Checkout");
                                }
                            });
                            mCartCountTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CartDialogs dialog = new CartDialogs();
                                    dialog.show(getSupportFragmentManager(), "Checkout");
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

//        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart").child("cart-items").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(final DataSnapshot dataSnapshot) {
////                dataSnapshot.notify();
//                double _amount = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    try {
//                        count_cart = count_cart + Integer.parseInt(snapshot.child("quantity").getValue().toString());
//                        System.out.println("MANY: " + Integer.parseInt(snapshot.child("quantity").getValue().toString()));
//                        _amount = _amount + ((Double.parseDouble(String.valueOf(snapshot.child("product").child("price").getValue()))) * (Integer.parseInt(snapshot.child("quantity").getValue().toString())));
//
//
//                        System.out.println("Amount Cart " + count_cart);
//                    } catch (Exception e) {
//                        e.getMessage();
//                    }
//                }
//
//                int qu = count_cart;
//                if (qu == 0) {
//                    mCartCountTextView.setVisibility(View.GONE);
//                } else {
//                    mCartCountTextView.setVisibility(View.VISIBLE);
//                    mCartCountTextView.setText(String.valueOf(qu));
//                    btnCart.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            CartDialogs dialog = new CartDialogs();
//                            dialog.show(getSupportFragmentManager(), "Checkout");
//                        }
//                    });
//                    mCartCountTextView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            CartDialogs dialog = new CartDialogs();
//                            dialog.show(getSupportFragmentManager(), "Checkout");
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        //Checks if a user profile exists or not
        /*
        if (isNetworkAvailable()) {

            if (user != null) {
                String uid = user.getUid();
                mDatabaseUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                checkIfUserExist();
                //checkEmailVerification();
            }


        } else */

        if (!isNetworkAvailable()) {

            Snackbar snack = Snackbar.make(findViewById(R.id.linear), "No Connection Available, please check your internet settings and try again.",
                    Snackbar.LENGTH_INDEFINITE).setDuration(5000);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_message) {
            Intent intent = new Intent(MainActivity.this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        }
//        if (id == R.id.action_publications) {
//            Intent intent = new Intent(MainActivity.this, PublicationsActivity.class);
//            startActivity(intent);
//            return true;
//        }
        if (id == R.id.action_log_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new ProductFragment();
                case 1:
                    return new PromotionalContentFragment();
                default:
                    return new ProductFragment();
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Browse";
                case 1:
                    return "Promotions";
            }
            return null;
        }
    }


    private void checkIfUserExist() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            final String user_uid = mAuth.getCurrentUser().getUid();
            //Check if the value a user entered exists on the database or not
            mDatabaseUsers.child(user_uid).child("Practice_Number").addValueEventListener(new ValueEventListener() {
                //@param dataSnapshot returns the results
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Check if the result has a child on the database
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(MainActivity.this, "It does not Exist", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    /*public void checkEmailVerification() {
        if (!user.isEmailVerified()) {
            Intent setupIntent = new Intent(getApplicationContext(), LoginActivity.class);
            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupIntent);
            finish();
        }
    }*/

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

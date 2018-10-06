package com.msqhealth.main.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.msqhealth.main.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfirmCheckoutActivity extends AppCompatActivity {

    private EditText mFullNamesEditText, mBillingAddressEditText, mPhoneNumberEditText;
    private Button mConfirm;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView tvTC;
    private String user_id, address, name, telephone, termsLink;
    private ProgressDialog mProgressDialog;

    private int textlength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_checkout);

        if (isNetworkAvailable()) {

            mFullNamesEditText = findViewById(R.id.et_name);
            mBillingAddressEditText = findViewById(R.id.et_billing_address);
            mPhoneNumberEditText = findViewById(R.id.et_cell);
            mConfirm = findViewById(R.id.save);
            tvTC = findViewById(R.id.confirm_tc);

            mProgressDialog = new ProgressDialog(this);
            mAuth = FirebaseAuth.getInstance();

//        mUser = mAuth.getCurrentUser();
            user_id = mAuth.getCurrentUser().getUid();

            termsLink = "https://msq-health.firebaseapp.com";

            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
            mDatabase.keepSynced(true);

            mDatabase.child("billing_infomation").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        mDatabase.child("profile").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                address = (String) dataSnapshot.child("Suburb").getValue();
                                name = (String) dataSnapshot.child("Name").getValue();
                                telephone = (String) dataSnapshot.child("Telephone").getValue();

                                System.out.println("Name : " + name);

                                mFullNamesEditText.setText(name);
                                mBillingAddressEditText.setText(address);
                                mPhoneNumberEditText.setText(telephone);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        mDatabase.child("billing_infomation").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                address = (String) dataSnapshot.child("delivery_address").getValue();
                                name = (String) dataSnapshot.child("full_names").getValue();
                                telephone = (String) dataSnapshot.child("phone_number").getValue();

                                mFullNamesEditText.setText(name);
                                mBillingAddressEditText.setText(address);
                                mPhoneNumberEditText.setText(telephone);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            tvTC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent termsIntent = new Intent(getApplicationContext(), TermsAndCondtionsActivity.class);
                    termsIntent.putExtra("link", termsLink);
                    startActivity(termsIntent);
                }
            });

            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String full_names = mFullNamesEditText.getText().toString().trim();
                    String billing_address = mBillingAddressEditText.getText().toString().trim();
                    String cell_number = mPhoneNumberEditText.getText().toString().trim();

                    if (!TextUtils.isEmpty(full_names)) {

                        if (!TextUtils.isEmpty(billing_address) && billing_address.length() >= 15) {

                            if (!TextUtils.isEmpty(cell_number)) {

                                mProgressDialog.setMessage("Updating Delivery Information ...");
                                mProgressDialog.show();

                                mDatabase.child("billing_infomation").child("full_names").setValue(full_names);
                                mDatabase.child("billing_infomation").child("delivery_address").setValue(billing_address);
                                mDatabase.child("billing_infomation").child("phone_number").setValue(cell_number).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mProgressDialog.dismiss();
                                            startActivity(new Intent(getApplicationContext(), PaymentGatewayWebView.class));
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                mPhoneNumberEditText.setError("Field empty");
                            }
                        } else {
                            mBillingAddressEditText.setError("Field empty");
                        }
                    } else {
                        mFullNamesEditText.setError("Field empty");
                    }
                }
            });
        } else if (!isNetworkAvailable()) {

            Snackbar snack = Snackbar.make(findViewById(R.id.relative_layout), getApplicationContext().getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE).setDuration(10000);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();
        }

    }

    public void phoneValidation() {
        mPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                String text = mPhoneNumberEditText.getText().toString();
                textlength = mPhoneNumberEditText.getText().length();

                if (text.endsWith(" "))
                    return;

                if (textlength == 1) {
                    if (!text.contains("(")) {
                        mPhoneNumberEditText.setText(new StringBuilder(text).insert(text.length() - 1, "(").toString());
                        mPhoneNumberEditText.setSelection(mPhoneNumberEditText.getText().length());
                    }

                } else if (textlength == 5) {

                    if (!text.contains(")")) {
                        mPhoneNumberEditText.setText(new StringBuilder(text).insert(text.length() - 1, ")").toString());
                        mPhoneNumberEditText.setSelection(mPhoneNumberEditText.getText().length());
                    }

                } else if (textlength == 6 || textlength == 10) {
                    mPhoneNumberEditText.setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
                    mPhoneNumberEditText.setSelection(mPhoneNumberEditText.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

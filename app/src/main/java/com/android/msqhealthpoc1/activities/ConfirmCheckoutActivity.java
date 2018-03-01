package com.android.msqhealthpoc1.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.msqhealthpoc1.R;
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

    private String user_id, address, name;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_checkout);

        mFullNamesEditText = findViewById(R.id.et_name);
        mBillingAddressEditText = findViewById(R.id.et_billing_address);
        mPhoneNumberEditText = findViewById(R.id.et_cell);
        mConfirm = findViewById(R.id.save);

        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

//        mUser = mAuth.getCurrentUser();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                address = (String) dataSnapshot.child("location_address").getValue();
                name = (String) dataSnapshot.child("name").getValue();

                System.out.println("Name : " + name);

                mFullNamesEditText.setText(name);
                mBillingAddressEditText.setText(address);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String full_names = mFullNamesEditText.getText().toString().trim();
                String billing_address = mBillingAddressEditText.getText().toString().trim();
                String cell_number = mPhoneNumberEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(full_names))  {

                    if (!TextUtils.isEmpty(billing_address) ) {

                        if (!TextUtils.isEmpty(cell_number)) {

                            mProgressDialog.setMessage("Updating Billing Information ...");
                            mProgressDialog.show();

                            mDatabase.child("billing_infomation").child("billing_address").setValue(billing_address);
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

    }
}

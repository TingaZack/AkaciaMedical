package com.android.msqhealthpoc1.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Admin on 22/02/2018.
 */

public class ContactUsActivity extends AppCompatActivity {

    private DatabaseReference mUsersDatabaseReference, mMessagesDatabaseReference;

    private String user_name, user_occupation, user_address, telephone;

    private Button mSendMessageButton;
    private EditText mMessageEditText;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        if (isNetworkAvailable()) {

            mAuth = FirebaseAuth.getInstance();
            final String uid = mAuth.getCurrentUser().getUid();
            final String email = mAuth.getCurrentUser().getEmail();

            mSendMessageButton = findViewById(R.id.btn_send_message);
            mMessageEditText = findViewById(R.id.et_message);
            mProgressDialog = new ProgressDialog(this);

            Toast.makeText(this, "uid: " + uid, Toast.LENGTH_SHORT).show();

            if (uid != null) {
                System.out.println("UID: " + uid);
                mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                mUsersDatabaseReference.keepSynced(true);
                mMessagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("messages");
                mMessagesDatabaseReference.keepSynced(true);

                mUsersDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user_name = (String) dataSnapshot.child("Name").getValue();
                        user_occupation = (String) dataSnapshot.child("Speciality").getValue();
                        user_address = (String) dataSnapshot.child("Suburb").getValue();
                        telephone = (String) dataSnapshot.child("Telephone").getValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.getMessage();
                    }
                });

                mSendMessageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String message_edittext = mMessageEditText.getText().toString().trim();

                        if (!TextUtils.isEmpty(message_edittext)) {

                            mProgressDialog.setMessage("Sending Message ...");
                            mProgressDialog.show();

                            DatabaseReference mReference = mMessagesDatabaseReference.push();
                            mReference.child("user_name").setValue(user_name);
                            mReference.child("user_email").setValue(email);
                            mReference.child("user_location").setValue(user_address);
                            mReference.child("user_occupation").setValue(user_occupation);
                            mReference.child("telephone").setValue(telephone);
                            mReference.child("timeStamp").setValue(getCurrentTimeStamp());
                            mReference.child("uid").setValue(uid);
                            mReference.child("message").setValue(message_edittext).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();

                                        View mView = LayoutInflater.from(ContactUsActivity.this).inflate(R.layout.check_dialog_box, null);

                                        Button mOkayButton;

                                        android.support.v7.app.AlertDialog.Builder aBuilder = new android.support.v7.app.AlertDialog.Builder(ContactUsActivity.this, R.style.CustomDialog);
                                        aBuilder.setView(mView);

                                        mOkayButton = (Button) mView.findViewById(R.id.dialogDone);

                                        mOkayButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            }
                                        });
                                        android.support.v7.app.AlertDialog alert = aBuilder.create();
                                        alert.show();
                                    }
                                }
                            });
                        } else {
//                            Toast.makeText(ContactUsActivity.this, "Message Field is empty.", Toast.LENGTH_SHORT).show();
                            mMessageEditText.setError("This field is mandatory");
                        }
                    }
                });
            }
        } else if (!isNetworkAvailable()) {

            Snackbar snack = Snackbar.make(findViewById(R.id.relative_layout), "No Connection Available, please check your internet settings and try again.", Snackbar.LENGTH_INDEFINITE)
                    .setDuration(5000);

            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();
        }

    }

    //Check the availability if the network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private Long getCurrentTimeStamp() {
        Long timestamp = System.currentTimeMillis() / 1000;
        return timestamp;
    }
}

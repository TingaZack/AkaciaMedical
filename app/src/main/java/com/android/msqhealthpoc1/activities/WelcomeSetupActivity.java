package com.android.msqhealthpoc1.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.helpers.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WelcomeSetupActivity extends AppCompatActivity {

    private LinearLayout mInsertPractice, mRegisterPractice;
    private DatabaseReference mDatabasePractice, mUsersDatabase;
    private FirebaseAuth mAuth;
    private EditText eSearchPractice, eSpecialityName;
    TextInputLayout eSpeciality;
    private Button btnSave;
    private ProgressDialog progressDialog;

    private String name, practice_number, suburb, telephone;
    private String email;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_setup);

        if (isNetworkAvailable()) {

            progressDialog = new ProgressDialog(this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            mInsertPractice = findViewById(R.id.practice_number_insert);
            mRegisterPractice = findViewById(R.id.practice_number_registration);

            eSearchPractice = findViewById(R.id.practice_number);
            eSpeciality = findViewById(R.id.occupation_textinput);
            eSpecialityName = findViewById(R.id.occupation);
            btnSave = findViewById(R.id.save);

            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {

                email = mAuth.getCurrentUser().getEmail();

                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                mDatabasePractice = FirebaseDatabase.getInstance().getReference().child("doctors_practice_numbers");
                mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

                mUsersDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.child("Practice_Number").getValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                checkEmailVerification();
                checkIfUserExist();

                eSearchPractice.addTextChangedListener(new TextWatcher() {
                    int prevL = 0;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(final CharSequence s, int start, int before, int count) {
                        System.out.println("CHAR COUNT: " + s.length());

                        if (s.length() == 15) {
//                    Toast.makeText(WelcomeSetupActivity.this, "Done", Toast.LENGTH_SHORT).show();

                            final Query query = mDatabasePractice.orderByChild("PRACTICE_NUMBER").equalTo(eSearchPractice.getText().toString());
                            progressDialog.setMessage("Searching for practice. Please wait...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataSnapshot.child("Practice_Number").getValue();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {



                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            name = (String) snapshot.child("NAME").getValue();
                                            practice_number = (String) snapshot.child("PRACTICE_NUMBER").getValue();
                                            suburb = (String) snapshot.child("SUBURB").getValue();
                                            telephone = (String) snapshot.child("TELEPHONE").getValue();

                                            System.out.println("Doctor's Details: \nName" + name + " \n " + "Practice Number: " + practice_number + "  \n" +
                                                    "\nSuburb: " + suburb + "\nTelephone: " + telephone);

                                            Toast.makeText(WelcomeSetupActivity.this, "Practice " + practice_number + " Found", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();

                                            eSpeciality.setVisibility(View.VISIBLE);

                                            btnSave.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    if (!TextUtils.isEmpty(eSpecialityName.getText().toString().trim()) && !TextUtils.isEmpty(eSearchPractice.getText().toString().trim())) {

//                                                        progressDialog.setMessage("Saving Information ...");
//                                                        progressDialog.show();
                                                        query.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot Snapshot) {

                                                                if (Snapshot.exists()) {
                                                                    mUsersDatabase.child("Name").setValue(name);
                                                                    mUsersDatabase.child("Suburb").setValue(suburb);
                                                                    mUsersDatabase.child("Telephone").setValue(telephone);
                                                                    mUsersDatabase.child("Speciality").setValue(eSpecialityName.getText().toString());
                                                                    mUsersDatabase.child("Email").setValue(email);
                                                                    mUsersDatabase.child("Practice_Number").setValue(practice_number).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
//                                                                                progressDialog.dismiss();
                                                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                                finish();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Your Practice Number could not be found. " +
                                                "Please contact MSQ if this might be a mistake.", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        eSpeciality.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        int length = s.length();
                        if ((prevL < length) && (length == 3 || length == 7 || length == 11)) {
                            s.append(",");
                        }

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (s.length() != 15) {
                                    eSearchPractice.setError("Length should be 12 digits");
                                }
                            }
                        });
                    }
                });
            }
        } else if (!isNetworkAvailable()) {


            Snackbar snack = Snackbar.make(findViewById(R.id.relative_layout), "No Connection Available, please check your internet settings and try again.", Snackbar.LENGTH_INDEFINITE).setDuration(10000);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            snack.show();
        }
    }

    private void checkIfUserExist() {

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Hello" + mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
            Intent setupIntent = new Intent(WelcomeSetupActivity.this, LoginActivity.class);
            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupIntent);
        }
    }


    public void checkEmailVerification() {
        if (!mFirebaseUser.isEmailVerified()) {
            Intent setupIntent = new Intent(getApplicationContext(), LoginActivity.class);
            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupIntent);
            Toast.makeText(this, "Please verify the email", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

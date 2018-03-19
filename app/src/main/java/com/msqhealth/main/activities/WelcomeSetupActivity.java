package com.msqhealth.main.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msqhealth.main.R;
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
    private String pract_number = null;

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
                final DatabaseReference mUsersData = FirebaseDatabase.getInstance().getReference().child("users");

                checkEmailVerification();
                checkIfUserExist();

                eSearchPractice.addTextChangedListener(new TextWatcher() {
                    int prevL = 0;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(final CharSequence s, final int start, int before, int count) {
                        System.out.println("CHAR COUNT: " + s.length());

                        if (s.length() == 7) {
                            final Query query = mDatabasePractice.orderByChild("PRACTICE_NUMBER").equalTo(eSearchPractice.getText().toString());
                            progressDialog.setMessage("Searching for practice. Please wait...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            final Query queryPracticeNumber = mUsersData.orderByChild("Practice_Number").equalTo(eSearchPractice.getText().toString());
                            queryPracticeNumber.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Check first if the Practice Number already exist or not
                                    if (dataSnapshot.exists()) {
                                        progressDialog.dismiss();
                                        //If the practice number already exist, it displays the button to navigate to login
                                        btnSave.setVisibility(View.VISIBLE);
                                        eSpeciality.setVisibility(View.GONE);
                                        btnSave.setText("Login");
                                        btnSave.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                finish();
                                            }
                                        });
                                    } else if (!dataSnapshot.exists()){
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //Check if the practice number matches our practice numbers database
                                                if (dataSnapshot.exists()) {
                                                    btnSave.setText("Save");
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        name = (String) snapshot.child("NAME").getValue();
                                                        practice_number = (String) snapshot.child("PRACTICE_NUMBER").getValue();
                                                        suburb = (String) snapshot.child("SUBURB").getValue();
                                                        telephone = (String) snapshot.child("TELEPHONE").getValue();

                                                        System.out.println("Doctor's Details: \nName" + name + " \n " + "Practice Number: " + practice_number + "  \n" +
                                                                "\nSuburb: " + suburb + "\nTelephone: " + telephone);

                                                        progressDialog.dismiss();

                                                        eSpeciality.setVisibility(View.VISIBLE);

                                                        btnSave.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                                if (!TextUtils.isEmpty(eSpecialityName.getText().toString().trim()) && !TextUtils.isEmpty(eSearchPractice.getText().toString().trim())) {

                                                                    progressDialog.setMessage("Saving Information ...");
                                                                    progressDialog.show();
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
                                                                                            progressDialog.dismiss();
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
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        int length = s.length();

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (s.length() != 7) {
                                    eSearchPractice.setError("Length should be 7 digits");
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

//    private void checkIfUserExist() {
//
//        if (mAuth.getCurrentUser() == null) {
//            Toast.makeText(this, "Hello" + mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
//            Intent setupIntent = new Intent(WelcomeSetupActivity.this, LoginActivity.class);
//            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(setupIntent);
//        }
//    }

    private void showDialog() {
        View mView = LayoutInflater.from(WelcomeSetupActivity.this).inflate(R.layout.deleted_custom_dialog, null);

        TextView successView = mView.findViewById(R.id.tv_success);
        ImageButton imageButton = mView.findViewById(R.id.dialogDone);
        android.support.v7.app.AlertDialog.Builder aBuilder = new android.support.v7.app.AlertDialog.Builder(WelcomeSetupActivity.this, R.style.CustomDialog);
        aBuilder.setView(mView);

        successView.setText("Practice Number Already Exist");

        final android.support.v7.app.AlertDialog alert = aBuilder.create();
        alert.show();

        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 1000);

    }


    public void checkEmailVerification() {
        if (!mFirebaseUser.isEmailVerified()) {
            Intent setupIntent = new Intent(getApplicationContext(), LoginActivity.class);
            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupIntent);
            Toast.makeText(this, "Please verify the email", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void checkIfUserExist() {

        if (mAuth.getCurrentUser() != null) {

            final String user_uid = mAuth.getCurrentUser().getUid();
            //Check if the value a user entered exists on the database or not
//            mUsersDatabase.addValueEventListener(new ValueEventListener() {
//                //@param dataSnapshot returns the results
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    //Check if the result has a child on the database
//                    if (!dataSnapshot.child(user_uid).hasChild("Practice_Number")) {
//                        Intent setupIntent = new Intent(WelcomeSetupActivity.this, LoginActivity.class);
//                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(setupIntent);
//                        finish();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

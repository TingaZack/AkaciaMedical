package com.msqhealth.main.activities.authentication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.msqhealth.main.R;
import com.msqhealth.main.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msqhealth.main.activities.TermsAndCondtionsActivity;
import com.msqhealth.main.fragments.authentication.ForgotPasswordFragment;

import es.dmoral.toasty.Toasty;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegistrationActivityFragment extends Fragment {

/*
    private EditText eSearchPractice, eEmailAddress, ePassword;
    private Button btnSave;
    private ProgressDialog progressDialog;
    TextView tvTC;

    String name, practice_number, suburb, telephone;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private DatabaseReference mDatabasePractice, mUsersDatabase;
    */

    EditText mUserEmail, mUserPassword;
    Button btnRegister;

    TextView tvTC;
    private ProgressDialog pDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            checkFieldsForEmptyValues();
        }
    };

    public RegistrationActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Authentication", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mUserEmail = view.findViewById(R.id.email);
        mUserPassword = view.findViewById(R.id.password);

        tvTC = view.findViewById(R.id.login_tc);
        tvTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TermsAndCondtionsActivity.class));
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Please wait ...");
        pDialog.setCancelable(false);


        btnRegister = view.findViewById(R.id.register);

        btnRegister.setEnabled(false);
        mUserEmail.addTextChangedListener(mTextWatcher);
        mUserPassword.addTextChangedListener(mTextWatcher);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                mAuth.createUserWithEmailAndPassword(mUserEmail.getText().toString().trim(), mUserPassword.getText().toString().trim())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pDialog.dismiss();
                                if (task.isSuccessful()) {
//                                    getFragmentManager().beginTransaction().replace(R.id.container, new NewPracticeRegistration()).commit();
                                    startActivity(new Intent(getActivity(), NewPracticeRegistration.class));
                                    getActivity().finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        checkFieldsForEmptyValues();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    protected void checkFieldsForEmptyValues() {
        // TODO Auto-generated method stub
        String text1 = mUserEmail.getText().toString().trim();
        String text2 = mUserPassword.getText().toString().trim();

        if ((TextUtils.isEmpty(text1)) || (TextUtils.isEmpty(text2))) {
            btnRegister.setEnabled(false);
        } else if ((TextUtils.getTrimmedLength(text2) < 6)) {
            btnRegister.setEnabled(false);
        } else {
            btnRegister.setEnabled(true);
        }
    }
//
//
//        if (isNetworkAvailable()) {
//
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//
//            eSearchPractice = view.findViewById(R.id.practice_number);
//            eEmailAddress = view.findViewById(R.id.email);
//            ePassword = view.findViewById(R.id.password);
//            btnSave = view.findViewById(R.id.save);
//
//            tvTC = view.findViewById(R.id.register_tc);
//            tvTC.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(getActivity(), TermsAndCondtionsActivity.class));
//                }
//            });
//
//            mAuth = FirebaseAuth.getInstance();
//
//
//            mDatabasePractice = FirebaseDatabase.getInstance().getReference().child("doctors_practice_numbers");
//            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
//
//
//            mDatabasePractice = FirebaseDatabase.getInstance().getReference().child("doctors_practice_numbers");
//            final DatabaseReference mUsersData = FirebaseDatabase.getInstance().getReference().child("users");
//
//
//            eSearchPractice.addTextChangedListener(new TextWatcher() {
//                int prevL = 0;
//
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(final CharSequence s, final int start, int before, int count) {
//                    (view.findViewById(R.id.practice_found)).setVisibility(View.GONE);
//                    if (s.length() == 7) {
//                        final Query query = mDatabasePractice.orderByChild("PRACTICE_NUMBER").equalTo(eSearchPractice.getText().toString());
//                        progressDialog.setMessage("Searching for practice. Please wait...");
//                        progressDialog.setCancelable(false);
//                        progressDialog.show();
//
//                        final Query queryPracticeNumber = mUsersData.orderByChild("Practice_Number").equalTo(eSearchPractice.getText().toString());
//                        queryPracticeNumber.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                //Check first if the Practice Number already exist or not
//                                if (dataSnapshot.exists()) {
//                                    progressDialog.dismiss();
//                                    //If the practice number already exist, it displays the button to navigate to login
//                                    ((TextView) view.findViewById(R.id.practice_found)).setText("Practice number already taken.");
//                                    (view.findViewById(R.id.practice_found)).setVisibility(View.VISIBLE);
//
//                                    eSearchPractice.setError("Practice number already taken");
//
//
//                                } else {
//                                    query.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            //Check if the practice number matches our practice numbers database
//                                            if (dataSnapshot.exists()) {
//                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                                    name = (String) snapshot.child("NAME").getValue();
//                                                    practice_number = (String) snapshot.child("PRACTICE_NUMBER").getValue();
//                                                    suburb = (String) snapshot.child("SUBURB").getValue();
//                                                    telephone = (String) snapshot.child("TELEPHONE").getValue();
//
//                                                    progressDialog.dismiss();
//                                                    btnSave.setVisibility(View.VISIBLE);
//                                                    eSearchPractice.setEnabled(false);
//                                                    btnSave.setText("Sign up");
//
//                                                    eEmailAddress.addTextChangedListener(mTextWatcher);
//                                                    ePassword.addTextChangedListener(mTextWatcher);
//
//                                                    checkFieldsForEmptyValues();
//
//                                                    ((TextView) view.findViewById(R.id.practice_number_textview)).setText(name);
//                                                    ((TextView) view.findViewById(R.id.practice_question)).setText("Please insert an email and password to complete registration.");
//
//                                                    ((LinearLayout) view.findViewById(R.id.email_password_layout)).setVisibility(View.VISIBLE);
//                                                    btnSave.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            if(isValidEmail(eEmailAddress.getText().toString().trim())) {
//                                                                signUpNewUser();
//                                                            } else {
//                                                                eEmailAddress.setError("Invalid email address");
//                                                            }
//                                                        }
//                                                    });
//                                                }
//                                            } else {
//                                                //Insert Dialog box here
//                                                progressDialog.dismiss();
//                                                showAlertDialog().show();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(final Editable s) {
//                    if (s.length() != 7) {
//                        eSearchPractice.setError("Enter last 7 numbers");
//                    }
//                }
//            });
//
//        } else if (!isNetworkAvailable()) {
//
//
//            Snackbar snack = Snackbar.make(view.findViewById(R.id.relative_layout), "No Connection Available, please check your internet settings and try again.", Snackbar.LENGTH_INDEFINITE).setDuration(10000);
//            snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
//            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
//            params.gravity = Gravity.TOP;
//            view.setLayoutParams(params);
//            snack.show();
//        }
//
//
//        return view;
//    }
//
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
//
//
//    public AlertDialog showAlertDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(R.string.practice_number_not_found)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        getFragmentManager().beginTransaction().replace(R.id.container, new NewPracticeRegistration()).commit();
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                        dialog.dismiss();
//                        eSearchPractice.setText("");
//                    }
//                });
//        // Create the AlertDialog object and return it
//
//        return builder.create();
//
//    }
//
//
//    public void signUpNewUser() {
//        progressDialog.setMessage("Signing up. Please wait...");
//        progressDialog.show();
//        mAuth.createUserWithEmailAndPassword(eEmailAddress.getText().toString().trim(), ePassword.getText().toString().trim())
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(getActivity(), task.getException().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//
//                            final Query query = mDatabasePractice.orderByChild("PRACTICE_NUMBER").equalTo(eSearchPractice.getText().toString().trim());
//
//                            query.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    //Check if the practice number matches our practice numbers database
//                                    if (dataSnapshot.exists()) {
//                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                            String name = (String) snapshot.child("NAME").getValue();
//                                            String practice_number = (String) snapshot.child("PRACTICE_NUMBER").getValue();
//                                            String suburb = (String) snapshot.child("SUBURB").getValue();
//                                            String telephone = (String) snapshot.child("TELEPHONE").getValue();
//
//                                            progressDialog.setMessage("Saving practice information...");
//                                            mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").setValue(name);
//                                            mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Suburb").setValue(suburb);
//                                            mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Telephone").setValue(telephone);
//                                            mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Email").setValue(eEmailAddress.getText().toString());
//                                            mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Practice_Number").setValue(practice_number).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    progressDialog.dismiss();
//                                                    if (task.isSuccessful()) {
//                                                        getActivity().finish();
//                                                        Intent intent = new Intent(getActivity(), MainActivity.class);
//                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                        startActivity(intent);
//                                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
//                                                    }
//                                                }
//                                            });
//                                        }
//
//                                    }
//                                }
//
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//
//                        }
//                    }
//                });
//    }
//
//    protected void checkFieldsForEmptyValues() {
//        // TODO Auto-generated method stub
//        String text1 = eEmailAddress.getText().toString().trim();
//        String text2 = ePassword.getText().toString().trim();
//
//        if ((TextUtils.isEmpty(text1)) || (TextUtils.isEmpty(text2))) {
//            btnSave.setEnabled(false);
//        } else if ((TextUtils.getTrimmedLength(text2) < 6)) {
//            btnSave.setEnabled(false);
//            ePassword.setError("Password length should be 6 or more characters");
//        } else {
//            btnSave.setEnabled(true);
//        }
//    }
//
//
//    private TextWatcher mTextWatcher = new TextWatcher() {
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before,
//                                  int count) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count,
//                                      int after) {
//            // TODO Auto-generated method stub
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // TODO Auto-generated method stub
//            checkFieldsForEmptyValues();
//        }
//    };
//
//    private static boolean isValidEmail(String email) {
//        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
//    }

}

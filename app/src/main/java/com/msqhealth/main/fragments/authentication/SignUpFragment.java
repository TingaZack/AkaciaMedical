package com.msqhealth.main.fragments.authentication;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.msqhealth.main.R;
import com.msqhealth.main.activities.MainActivity;
import com.msqhealth.main.activities.TermsAndCondtionsActivity;
import com.msqhealth.main.helpers.PrefManager;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    TextView tvTC;
    PrefManager prefManager;
    private EditText mUserEmail, mUserPassword;
    private Button btnSignUp;
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private DatabaseReference mDatabasePractice, mUsersDatabase;

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

    public static final SignUpFragment newInstance(String value) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString("practice_number", value);
        fragment.setArguments(args);
        return fragment;
    }


    public SignUpFragment() {
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
                // ...
            }
        };

        prefManager = new PrefManager(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        mUserEmail = view.findViewById(R.id.email);
        mUserPassword = view.findViewById(R.id.password);
        tvTC = view.findViewById(R.id.signup_tc);
        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Signing up");
        pDialog.setCancelable(false);

        mDatabasePractice = FirebaseDatabase.getInstance().getReference().child("doctors_practice_numbers");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        btnSignUp = view.findViewById(R.id.sign_up);

        btnSignUp.setEnabled(false);
        mUserEmail.addTextChangedListener(mTextWatcher);
        mUserPassword.addTextChangedListener(mTextWatcher);

        tvTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TermsAndCondtionsActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                mAuth.createUserWithEmailAndPassword(mUserEmail.getText().toString().trim(), mUserPassword.getText().toString().trim())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getActivity(), task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
//                                    prefManager.setFirstTimeSignUp(false);
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                                    final Query query = mDatabasePractice.orderByChild("PRACTICE_NUMBER").equalTo(getArguments().getString("practice_number"));

                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //Check if the practice number matches our practice numbers database
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    String name = (String) snapshot.child("NAME").getValue();
                                                    String practice_number = (String) snapshot.child("PRACTICE_NUMBER").getValue();
                                                    String suburb = (String) snapshot.child("SUBURB").getValue();
                                                    String telephone = (String) snapshot.child("TELEPHONE").getValue();

                                                    pDialog.setMessage("Saving practice information...");
                                                    mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").setValue(name);
                                                    mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Suburb").setValue(suburb);
                                                    mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Telephone").setValue(telephone);
                                                    mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Email").setValue(mUserEmail.getText().toString());
                                                    mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Practice_Number").setValue(practice_number).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            pDialog.dismiss();
                                                            if (task.isSuccessful()) {
                                                                getActivity().finish();
                                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                                            }
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
                            }
                        });
            }
        });

        checkFieldsForEmptyValues();

        return view;
    }

    protected void checkFieldsForEmptyValues() {
        // TODO Auto-generated method stub
        String text1 = mUserEmail.getText().toString().trim();
        String text2 = mUserPassword.getText().toString().trim();

        if ((TextUtils.isEmpty(text1)) || (TextUtils.isEmpty(text2))) {
            btnSignUp.setEnabled(false);
        } else if ((TextUtils.getTrimmedLength(text2) < 6)) {
            btnSignUp.setEnabled(false);
            mUserPassword.setError("Password length should be more than 6 characters");
        } else {
            btnSignUp.setEnabled(true);
        }
    }

}

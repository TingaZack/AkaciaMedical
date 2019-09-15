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

import com.google.android.gms.tasks.OnFailureListener;
import com.msqhealth.main.R;
import com.msqhealth.main.activities.MainActivity;
import com.msqhealth.main.activities.TermsAndCondtionsActivity;
import com.msqhealth.main.activities.authentication.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msqhealth.main.app.AppController;

import es.dmoral.toasty.Toasty;

public class LoginFragment extends Fragment {

    EditText mUserEmail, mUserPassword;
    Button btnSignIn;

    TextView tvTC;
    private ProgressDialog pDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;

    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.activity_login, container, false);

        mUserEmail = view.findViewById(R.id.email);
        mUserPassword = view.findViewById(R.id.password);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Signing in...");

        view.findViewById(R.id.reset_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container, new ForgotPasswordFragment()).addToBackStack("forgot password").commit();
            }
        });


        btnSignIn = view.findViewById(R.id.sign_in);


        checkFieldsForEmptyValuesAndSubmit();

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

    protected void checkFieldsForEmptyValuesAndSubmit() {
        // TODO Auto-generated method stub

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = mUserEmail.getText().toString().trim();
                String text2 = mUserPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(text1)) {
                    if (!TextUtils.isEmpty(text2)) {
                        pDialog.show();
                        mAuth.signInWithEmailAndPassword(mUserEmail.getText().toString().trim(), mUserPassword.getText().toString().trim())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        pDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                                            getActivity().finish();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pDialog.dismiss();
                                Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        mUserPassword.setError(getString(R.string.field_empty));
                    }
                } else {
                    mUserEmail.setError(getString(R.string.field_empty));
                }

            }
        });
    }
}
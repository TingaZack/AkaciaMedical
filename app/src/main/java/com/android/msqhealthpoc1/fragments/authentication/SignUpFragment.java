package com.android.msqhealthpoc1.fragments.authentication;


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
import android.widget.Toast;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.WelcomeActivity;
import com.android.msqhealthpoc1.helpers.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    PrefManager prefManager;
    private EditText mUserEmail, mUserPassword;
    private Button btnSignUp;
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
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

        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Signing up");
        pDialog.setCancelable(false);


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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                mAuth.createUserWithEmailAndPassword(mUserEmail.getText().toString().trim(), mUserPassword.getText().toString().trim())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pDialog.dismiss();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getActivity(), task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    prefManager.setFirstTimeSignUp(false);
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(getActivity(), "Email verification link sent. Please verify your account to activate it.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                    getActivity().startActivity(new Intent(getActivity(), WelcomeActivity.class));
                                    getActivity().finish();
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
        } else {
            btnSignUp.setEnabled(true);
        }
    }

}

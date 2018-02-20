package com.android.msqhealthpoc1.fragments.authentication;


import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {


    EditText mUserEmail;
    Button btnResetPassword;
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

    public ForgotPasswordFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        mUserEmail = view.findViewById(R.id.email);


        pDialog = new ProgressDialog(getActivity());
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setMessage("Sending reset link");
        pDialog.setCancelable(false);

        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        btnResetPassword = view.findViewById(R.id.reset_password);

        mUserEmail.addTextChangedListener(mTextWatcher);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                mAuth.sendPasswordResetEmail(mUserEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pDialog.dismiss();
                                if (task.isSuccessful()) {
                                    getFragmentManager().popBackStack();
                                    Log.d("Email Sent", "Email sent.");
                                    Toast.makeText(getActivity(), "Password reset link sent. Please check your email and follow instructions there", Toast.LENGTH_LONG).show();
                                } else {
                                    //Please try again
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

        if (TextUtils.isEmpty(text1)) {
            btnResetPassword.setEnabled(false);
        } else {
            btnResetPassword.setEnabled(true);
        }
    }


}

package com.msqhealth.main.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.msqhealth.main.R;
import com.msqhealth.main.fragments.authentication.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_layout);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            getFragmentManager().beginTransaction().add(R.id.container, new LoginFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Authentication", "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // User is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

}

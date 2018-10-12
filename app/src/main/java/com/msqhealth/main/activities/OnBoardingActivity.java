package com.msqhealth.main.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.msqhealth.main.R;
import com.msqhealth.main.activities.authentication.RegistrationActivity;
import com.msqhealth.main.fragments.authentication.LoginFragment;
import com.msqhealth.main.helpers.PrefManager;

public class OnBoardingActivity extends AppCompatActivity {

    private Button mLoginButton, mRegisterButton, mBrowse;
    private Fragment newFragment;

    private PrefManager prefManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        prefManager = new PrefManager(getApplicationContext());
        if (!prefManager.isBrowseCatalogue()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    System.out.println("Authentication" + "onAuthStateChanged:signed_in");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    // User is signed out
                    System.out.println("Authentication" + "onAuthStateChanged:signed_out");
                }
            }
        };

        clickButtons();

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

    public void clickButtons() {

        mLoginButton = findViewById(R.id.login_button);
        mBrowse = findViewById(R.id.btn_browse_catalogue);
        mRegisterButton = findViewById(R.id.register_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        mBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setToBrowseCatalogue(false);
                System.out.println("CATALOGUE: " + prefManager.isBrowseCatalogue());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

}

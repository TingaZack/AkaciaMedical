package com.android.msqhealthpoc1.activities;

import android.app.Activity;
import android.os.Bundle;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.fragments.authentication.LoginFragment;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_layout);

        getFragmentManager().beginTransaction().add(R.id.container, new LoginFragment()).commit();
    }
}

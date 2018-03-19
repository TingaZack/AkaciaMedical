package com.msqhealth.main.activities.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.msqhealth.main.R;

public class RegistrationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.container_layout);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new RegistrationActivityFragment()).commit();
        }
    }

}

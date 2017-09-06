package com.android.msqhealthpoc1.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.android.msqhealthpoc1.R;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
    }
}

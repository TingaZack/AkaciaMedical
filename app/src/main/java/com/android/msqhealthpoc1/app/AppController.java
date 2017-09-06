package com.android.msqhealthpoc1.app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sihlemabaleka on 7/8/17.
 */

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

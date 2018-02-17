package com.android.msqhealthpoc1.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sihlemabaleka on 7/9/17.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "msq-health-onboarding";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeSignUp";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeSignUp(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeSignup() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}

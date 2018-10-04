package com.msqhealth.main.helpers;

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

    private static final String BROWSE_CATALOGUE_LAUNCH = "isBrowseCatalogue";
    private static final String FIRST_TIME_LAUNCH = "isFirstTimeRegister";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setToBrowseCatalogue(boolean browseCatalogue) {
        editor.putBoolean(BROWSE_CATALOGUE_LAUNCH, browseCatalogue);
        editor.commit();
    }

    public boolean isBrowseCatalogue() {
        return pref.getBoolean(BROWSE_CATALOGUE_LAUNCH, true);
    }

    public void setToFirstTimeRegister(boolean browseCatalogue) {
        editor.putBoolean(FIRST_TIME_LAUNCH, browseCatalogue);
        editor.commit();
    }

    public boolean isFirstTimeRegister() {
        return pref.getBoolean(FIRST_TIME_LAUNCH, true);
    }

}

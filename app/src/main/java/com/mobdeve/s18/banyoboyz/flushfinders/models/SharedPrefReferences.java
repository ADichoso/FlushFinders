package com.mobdeve.s18.banyoboyz.flushfinders.models;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefReferences {
    public static final String SHARED_PREFS = "SHARED_PREFS";
    public static final String ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY";
    public static final String ACCOUNT_EMAIL_KEY = "ACCOUNT_EMAIL_KEY";
    public static final String ACCOUNT_TYPE_KEY = "ACCOUNT_TYPE_KEY";
    public static final String ACCOUNT_PP_KEY = "ACCOUNT_PP_KEY";

    public static void clearSharedPreferences(Context context)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}

package com.mobdeve.s18.banyoboyz.flushfinders.helper;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Date;

public class SharedPrefReferences
{
    public static final String SHARED_PREFS = "SHARED_PREFS";
    public static final String ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY";
    public static final String ACCOUNT_EMAIL_KEY = "ACCOUNT_EMAIL_KEY";
    public static final String ACCOUNT_TYPE_KEY = "ACCOUNT_TYPE_KEY";
    public static final String ACCOUNT_PP_KEY = "ACCOUNT_PP_KEY";
    public static final String ACCOUNT_LOGIN_TIME_KEY = "ACCOUNT_LOGIN_TIME_KEY";

    public static final int daysLoginTime = 3;

    public static void clearSharedPreferences(Context context)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static boolean isLoginExpired(long loginTime)
    {
        return calculateDaysBetween(loginTime, (new Date()).getTime()) - 3 <= 0;
    }

    private static long calculateDaysBetween(long startTime, long endTime) {
        // Convert milliseconds to Date objects
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);

        // Calculate the difference in milliseconds
        long diffInMillis = endDate.getTime() - startDate.getTime();

        // Convert milliseconds to days
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        return diffInDays;
    }
}

package com.something.liberty;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserUtils
{
    private static final String PREFS_USERNAME = "USERNAME";
    private static final String DEFAULT_USERNAME = "username";

    public static String getUsername(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(PREFS_USERNAME,DEFAULT_USERNAME);
    }

    public static void setUsername(Context context,String username)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PREFS_USERNAME,username).commit();
    }
}

package com.something.liberty;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class UserUtils
{
    public static final String DEFAULT_USERNAME = "username";

    public static String getUsername(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_twitter_username),DEFAULT_USERNAME);
    }

    public static void setUsername(Context context,String username)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(context.getString(R.string.pref_twitter_username),username).commit();
    }

    private static String readTwitterUsername(Context context)
    {
        AccountManager accountManager = AccountManager.get(context);
        Account[] twitterAccounts = accountManager.getAccountsByType("com.twitter.android.auth.login");
        if(twitterAccounts.length > 0)
        {
            Account firstTwitterAccount = twitterAccounts[0];
            return firstTwitterAccount.name;
        }
        return null;
    }

    public static void updateUsernameIfUnset(Context context)
    {
        if(getUsername(context).equals(DEFAULT_USERNAME))
        {
            String username = readTwitterUsername(context);
            if(username != null)
            {
                setUsername(context,username);
            }
        }
    }
}

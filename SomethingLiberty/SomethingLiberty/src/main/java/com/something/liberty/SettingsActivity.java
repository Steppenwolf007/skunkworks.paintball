package com.something.liberty;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.something.liberty.messaging.GameMessagingService;

/**
 * @author Alexander Pringle
 */
public class SettingsActivity extends PreferenceActivity
{
    SharedPreferences.OnSharedPreferenceChangeListener twitterUsernameChangeListener = null;
    SharedPreferences mSharedPreferences = null;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        twitterUsernameChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
            {
                if(getString(R.string.pref_twitter_username).equals(key))
                {
                    GameMessagingService.stopService(getApplicationContext());
                    GameMessagingService.ensureServiceStarted(getApplicationContext());
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(twitterUsernameChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(twitterUsernameChangeListener);
    }
}
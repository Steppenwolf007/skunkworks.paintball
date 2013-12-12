package com.something.liberty.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.something.liberty.alerts.NotificationUtils;

public class GameMessageReciever extends BroadcastReceiver {

    public static String ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE = "HANDLE_ATTACK_RESPONSE_MESSAGE";
    public static String ACTION_HANDLE_KILLED_MESSAGE = "HANDLE_KILLED_MESSAGE";

    public GameMessageReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        Log.i("SomethingLiberty","in receiver");
        if(ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE.equals(intent.getAction()))
        {
            String responseType = intent.getStringExtra("responseType");
            String message = intent.getStringExtra("attackerMessage");
            notificationUtils.displayGenericNotification(responseType,message);
        }
        else if(ACTION_HANDLE_KILLED_MESSAGE.equals(intent.getAction()))
        {
            String message = intent.getStringExtra("message");
            notificationUtils.displayGenericNotification("Killed",message);
        }

    }
}

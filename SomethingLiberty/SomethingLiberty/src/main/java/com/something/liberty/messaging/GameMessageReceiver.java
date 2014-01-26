package com.something.liberty.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.something.liberty.R;
import com.something.liberty.alerts.NotificationUtils;

/**
 * @author Alexander Pringle
 */
public class GameMessageReceiver extends BroadcastReceiver {

    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_RESPONSE_TYPE = "responseType";
    public static final String EXTRA_ATTACKER_MESSAGE = "attackerMessage";
    public static final String EXTRA_NEWS_JSON = "news";

    public static String ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE = "HANDLE_ATTACK_RESPONSE_MESSAGE";
    public static String ACTION_HANDLE_SPLATTED_MESSAGE = "HANDLE_SPLATTED_MESSAGE";
    public static String ACTION_HANDLE_OUTGUNNER_MESSAGE = "HANDLE_OUTGUNNER_MESSAGE";
    public static final String ACTION_HANDLE_NEWS_MESSAGE = "HANDLE_NEWS_MESSAGE";

    public GameMessageReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationUtils notificationUtils = new NotificationUtils(context);

        if(ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE.equals(intent.getAction()))
        {
            String responseType = intent.getStringExtra(EXTRA_RESPONSE_TYPE);
            String message = intent.getStringExtra(EXTRA_ATTACKER_MESSAGE);
            notificationUtils.displayGenericNotification(responseType,message);
        }
        else if(ACTION_HANDLE_SPLATTED_MESSAGE.equals(intent.getAction()))
        {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            notificationUtils.displayGenericNotification(context.getString(R.string.splatted),message);
        }
        else if(ACTION_HANDLE_OUTGUNNER_MESSAGE.equals(intent.getAction()))
        {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            notificationUtils.displayGenericNotification(context.getString(R.string.self_defence),message);
        }

    }
}

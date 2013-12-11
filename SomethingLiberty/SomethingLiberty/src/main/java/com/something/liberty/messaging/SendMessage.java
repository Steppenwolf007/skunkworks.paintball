package com.something.liberty.messaging;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by alex on 11/12/13.
 */
public class SendMessage
{
    public static void sendAttackMessage(Context context,double longitude,double latitude)
    {
        Intent serviceIntent = new Intent(context,GameMessagingService.class);
        serviceIntent.setAction(GameMessagingService.ACTION_ATTACK);
        serviceIntent.putExtra("longitude",longitude);
        serviceIntent.putExtra("latitude",latitude);
        context.bindService(serviceIntent, new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },Context.BIND_AUTO_CREATE);
    }

    public static void sendLocationUpdate(Context context,double longitude,double latitude)
    {
        Intent serviceIntent = new Intent(context,GameMessagingService.class);
        serviceIntent.setAction(GameMessagingService.ACTION_UPDATE_LOCATION);
        serviceIntent.putExtra("longitude",longitude);
        serviceIntent.putExtra("latitude",latitude);
        context.bindService(serviceIntent,new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },Context.BIND_AUTO_CREATE);
    }
}

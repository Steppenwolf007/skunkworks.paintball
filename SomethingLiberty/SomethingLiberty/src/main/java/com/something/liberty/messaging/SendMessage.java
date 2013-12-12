package com.something.liberty.messaging;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.something.liberty.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMessage
{

    public static void sendAttackMessage(Context context,double longitude,double latitude)
    {
        JSONObject locationUpdate = null;
        try
        {
            locationUpdate = new JSONObject();
            locationUpdate.put("latitude",latitude);
            locationUpdate.put("longitude",longitude);
            locationUpdate.put("username", UserUtils.getUsername(context));
        }
        catch(JSONException e)
        {
            Log.e("SomethingLiberty", "GameMessagingServer : failed to create attack message");
            e.printStackTrace();
            return;
        }
        new SendJsonToTopicTask(GameMessagingService.MQTT_TOPIC_ATTACK).execute(locationUpdate);
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

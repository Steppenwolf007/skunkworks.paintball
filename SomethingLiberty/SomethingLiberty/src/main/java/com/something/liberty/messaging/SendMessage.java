package com.something.liberty.messaging;

import android.content.Context;
import android.util.Log;

import com.something.liberty.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMessage
{
    private static final String MQTT_TOPIC_REQUEST_NEWS = "something/requestNews";
    private static final String MQTT_TOPIC_LOCATION_UPDATE = "something/locationUpdate";
    private static final String MQTT_TOPIC_ATTACK = "something/attack";

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
        new SendJsonToTopicTask(MQTT_TOPIC_ATTACK).execute(locationUpdate);
    }

    public static void sendLocationUpdate(Context context,double longitude,double latitude)
    {
        JSONObject request = new JSONObject();
        try
        {
            request.put("longitude",longitude);
            request.put("latitude",latitude);
            request.put("username",UserUtils.getUsername(context));
        }
        catch(JSONException e)
        {
            Log.e("SomethingLiberty","Failed to send location update");
            e.printStackTrace();
        }
        new SendJsonToTopicTask(MQTT_TOPIC_LOCATION_UPDATE).execute(request);
    }

    public static void sendNewsRequest(Context context)
    {
        JSONObject request = new JSONObject();
        try
        {
            request.put("username",UserUtils.getUsername(context));
        }
        catch(JSONException e)
        {
            Log.e("SomethingLiberty","Failed to request news");
            e.printStackTrace();
        }
        new SendJsonToTopicTask(MQTT_TOPIC_REQUEST_NEWS).execute(request);
    }
}

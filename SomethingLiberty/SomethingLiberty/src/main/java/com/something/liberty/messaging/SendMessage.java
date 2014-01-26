package com.something.liberty.messaging;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.something.liberty.R;
import com.something.liberty.UserUtils;
import com.something.liberty.location.LocationUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMessage
{
    private static final String MQTT_TOPIC_REQUEST_NEWS = "something/requestNews";
    private static final String MQTT_TOPIC_LOCATION_UPDATE = "something/locationUpdate";
    private static final String MQTT_TOPIC_ATTACK = "something/attack";

    /**
     * Send an attack message using the last known device location
     */
    public static void sendAttackMessage(Context context)
    {
        LocationUtils locationUtils = new LocationUtils(context);
        Location lastKnownLocation = locationUtils.getLastKnownLocation();
        if(lastKnownLocation != null)
        {
            JSONObject locationUpdate = null;
            try
            {
                locationUpdate = new JSONObject();
                locationUpdate.put("latitude",lastKnownLocation.getLatitude());
                locationUpdate.put("longitude",lastKnownLocation.getLongitude());
                locationUpdate.put("username", UserUtils.getUsername(context));
            }
            catch(JSONException e)
            {
                Log.e("SomethingLiberty", "GameMessagingServer : failed to create attack message");
                e.printStackTrace();
                return;
            }
            new SendJsonToTopicTask(context,MQTT_TOPIC_ATTACK).execute(locationUpdate);
        }
        else
        {
            Log.d("SomethingLiberty","Cannot send attack message : No known location");
            Toast.makeText(context, context.getResources().getString(R.string.no_known_location), Toast.LENGTH_SHORT).show();
        }
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
        new SendJsonToTopicTask(context,MQTT_TOPIC_LOCATION_UPDATE).execute(request);
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
        new SendJsonToTopicTask(context,MQTT_TOPIC_REQUEST_NEWS).execute(request);
    }
}

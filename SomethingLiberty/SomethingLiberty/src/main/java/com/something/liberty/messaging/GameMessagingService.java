package com.something.liberty.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.something.liberty.MainActivity;
import com.something.liberty.UserUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class GameMessagingService extends Service implements NewGameMessageHandler, ConnectionLostHandler
{
    private static final String MQTT_TOPIC_KILLED = "something/killed/";
    private static final String MQTT_TOPIC_ATTACK_RESPONSE = "something/attResponse/";
    private static final String MQTT_TOPIC_NEWS = "something/news/";
    private static final String MQTT_TOPIC_OUTGUNNER = "something/outgunner/";

    public static final String MQTT_TOPIC_REQUEST_NEWS = "something/requestNews";
    public static final String MQTT_TOPIC_LOCATION_UPDATE = "something/locationUpdate";
    public static final String MQTT_TOPIC_ATTACK = "something/attack";

    public static final String ACTION_UPDATE_LOCATION = "UPDATE_LOCATION";
    public static final String ACTION_ATTACK = "ATTACK";

    private Handler uiThreadHandler = null;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(Service.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"SomethingLibertyWakeLock");
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(getApplicationContext(),"Service started",Toast.LENGTH_SHORT).show();
        uiThreadHandler = new Handler();

        final ConnectionLostHandler thisConnectionLostHandler = this;
        final NewGameMessageHandler thisMessageHandler = this;
        final String username = UserUtils.getUsername(this);
        new Thread(new Runnable(){
            @Override
            public void run() {

                MessagingUtils messagingUtils = MessagingUtils.getMessagingUtils();
                messagingUtils.subscribeToTopic(MQTT_TOPIC_KILLED + username,thisMessageHandler);
                messagingUtils.subscribeToTopic(MQTT_TOPIC_ATTACK_RESPONSE + username,thisMessageHandler);
                messagingUtils.subscribeToTopic(MQTT_TOPIC_NEWS + username, thisMessageHandler);
                messagingUtils.subscribeToTopic(MQTT_TOPIC_OUTGUNNER + username,thisMessageHandler);
                messagingUtils.setConnectionLostHandler(thisConnectionLostHandler);
            }
        }).run();


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        if(ACTION_UPDATE_LOCATION.equals(intent.getAction()))
        {
            double latitude = intent.getDoubleExtra("latitude",0);
            double longitude = intent.getDoubleExtra("longitude",0);
            if(latitude != 0 && longitude != 0)
            {
                sendLocationUpdate(latitude,longitude);
            }
        }
        return null;
    }

    @Override
    public void onNewGameMessage(final String topic, final MqttMessage message)
    {
        final Service thisService = this;
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                String messageString = new String(message.getPayload());
                Log.i("SomethingLiberty", "GameMessagingService : received : " + messageString);
                Toast.makeText(thisService,messageString,Toast.LENGTH_SHORT).show();

                if(topic.contains(MQTT_TOPIC_KILLED))
                {
                    handleKilledMessage(message);
                }
                else if(topic.contains(MQTT_TOPIC_ATTACK_RESPONSE))
                {
                    handleAttackResponseMessage(message);
                }
                else if(topic.contains(MQTT_TOPIC_NEWS))
                {
                    handleNewsMessage(message);
                }
                else if(topic.contains(MQTT_TOPIC_OUTGUNNER))
                {
                    handleOutgunnerMessage(message);
                }
            }
        });
    }

    @Override
    public void onConnectionLost(final Throwable cause)
    {
        final Service thisService = this;
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("SomethingLiberty", "GameMessagingService : Client disconnected : " + cause.getMessage());
                Toast.makeText(thisService,"Lost Mqtt connection",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleKilledMessage(MqttMessage killedMessage)
    {
        String payloadString = new String(killedMessage.getPayload());
        String messageToDisplay = null;
        try
        {
            JSONObject payloadObject = null;
            payloadObject = new JSONObject(payloadString);
            messageToDisplay = payloadObject.getString("message");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("SomethingLiberty","Failed to parse killed message");
            return;
        }

        Intent broadcastMessageIntent = new Intent();
        broadcastMessageIntent.setAction(GameMessageReciever.ACTION_HANDLE_KILLED_MESSAGE);
        broadcastMessageIntent.putExtra("message",messageToDisplay);
        sendOrderedBroadcast(broadcastMessageIntent, null);
    }

    private void handleAttackResponseMessage(MqttMessage attackResponseMessage)
    {
        String payloadString = new String(attackResponseMessage.getPayload());
        String responseResult = null;
        String messageToDisplay = null;
        try
        {
            JSONObject payloadObject = null;
            payloadObject = new JSONObject(payloadString);
            responseResult = payloadObject.getString("responseType");
            messageToDisplay = payloadObject.getString("attackerMessage");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("SomethingLiberty","Failed to parse attack response message");
            return;
        }

        Intent broadcastMessageIntent = new Intent();
        broadcastMessageIntent.setAction(GameMessageReciever.ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE);
        broadcastMessageIntent.putExtra("responseType", responseResult);
        broadcastMessageIntent.putExtra("attackerMessage",messageToDisplay);
        sendOrderedBroadcast(broadcastMessageIntent,null);
    }

    private void handleNewsMessage(MqttMessage newsMessage)
    {
        Intent broadcastMessageIntent = new Intent();
        broadcastMessageIntent.setAction(MainActivity.ACTION_HANDLE_NEWS_MESSAGE);
        broadcastMessageIntent.putExtra("news",new String(newsMessage.getPayload()));
        sendOrderedBroadcast(broadcastMessageIntent,null);
    }

    private void handleOutgunnerMessage(MqttMessage outgunnerMessage)
    {
        String messageString = null;
        try
        {
            JSONObject payloadObject = new JSONObject(new String(outgunnerMessage.getPayload()));
            messageString = payloadObject.getString("message");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("SomethingLiberty","Failed to parse outgunner message");
            return;
        }

        Intent broadcastMessageIntent = new Intent();
        broadcastMessageIntent.putExtra("message",messageString);
        broadcastMessageIntent.setAction(GameMessageReciever.ACTION_HANDLE_OUTGUNNER_MESSAGE);
        sendOrderedBroadcast(broadcastMessageIntent,null);
    }

    private void sendLocationUpdate(double latitude, double longitude)
    {
        JSONObject locationUpdate = null;
        try
        {
            locationUpdate = new JSONObject();
            locationUpdate.put("latitude",latitude);
            locationUpdate.put("longitude",longitude);
            locationUpdate.put("username", UserUtils.getUsername(this));
        }
        catch(JSONException e)
        {
            Log.e("SomethingLiberty","GameMessagingServer : failed to create location update message");
            e.printStackTrace();
            return;
        }
        MessagingUtils messagingUtils = MessagingUtils.getMessagingUtils();
        messagingUtils.sendMessage(MQTT_TOPIC_LOCATION_UPDATE,locationUpdate.toString());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
}
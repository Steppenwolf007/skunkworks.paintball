package com.something.liberty.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.something.liberty.UserUtils;
import com.something.liberty.alerts.NotificationUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class GameMessagingService extends Service implements NewGameMessageHandler, ConnectionLostHandler
{
    private static final String MQTT_TOPIC_KILLED = "something/killed/";
    private static final String MQTT_TOPIC_ATTACK_RESPONSE = "something/attackResponse/";
    private static final String MQTT_TOPIC_LOCATION_UPDATE = "something/locationUpdate";
    private static final String MQTT_TOPIC_ATTACK = "something/attack";

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

        new Thread(new Runnable(){
            @Override
            public void run() {

                MessagingUtils messagingUtils = MessagingUtils.getMessagingUtils();
                messagingUtils.subscribeToTopic(MQTT_TOPIC_KILLED + "alex_pringle",thisMessageHandler);
                messagingUtils.subscribeToTopic(MQTT_TOPIC_ATTACK_RESPONSE + "alex_pringle",thisMessageHandler);
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
        else if(ACTION_ATTACK.equals(intent.getAction()))
        {
            double latitude = intent.getDoubleExtra("latitude",0);
            double longitude = intent.getDoubleExtra("longitude",0);
            if(latitude != 0 && longitude != 0)
            {
                sendAttack(latitude, longitude);
            }
            else
            {
                Log.e("SomethingLiberty","GameMessagingService : received invalid attack request");
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

        NotificationUtils notificationUtils = new NotificationUtils(this);
        notificationUtils.displayGenericNotification("Killed",messageToDisplay);
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
            responseResult = payloadObject.getString("result");
            messageToDisplay = payloadObject.getString("attackerMessage");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("SomethingLiberty","Failed to parse attack response message");
            return;
        }

        NotificationUtils notificationUtils = new NotificationUtils(this);
        notificationUtils.displayGenericNotification(responseResult,messageToDisplay);
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

    private void sendAttack(double latitude, double longitude)
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
            Log.e("SomethingLiberty","GameMessagingServer : failed to create attack message");
            e.printStackTrace();
            return;
        }
        MessagingUtils messagingUtils = MessagingUtils.getMessagingUtils();
        messagingUtils.sendMessage(MQTT_TOPIC_ATTACK,locationUpdate.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
}
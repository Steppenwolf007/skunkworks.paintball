package com.something.liberty.messaging;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.something.liberty.UserUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;


public class GameMessagingService extends Service implements MessagingUtils.NewGameMessageHandler, MessagingUtils.ConnectionLostHandler
{
    public static UUID PEBBLE_APP_UUID = UUID.fromString("8e994e98-0427-4a18-8103-f6b7e5489782");
    private static final int PEBBLE_KEY_ATTACK = 1;
    private static final String PEBBLE_MESSAGE_TYPE_ATTACK = "ATTACK";

    private static final String MQTT_TOPIC_SPLATTED = "something/killed/";
    private static final String MQTT_TOPIC_ATTACK_RESPONSE = "something/attResponse/";
    private static final String MQTT_TOPIC_NEWS = "something/news/";
    private static final String MQTT_TOPIC_OUTGUNNER = "something/outgunner/";


    public static void ensureServiceStarted(Context context)
    {
        // check if service already running
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (GameMessagingService.class.getName().equals(service.service.getClassName()))
            {
                return;
            }
        }

        Intent intent = new Intent(context,GameMessagingService.class);
        context.startService(intent);
    }

    public static void stopService(Context context)
    {
        Intent intent = new Intent(context,GameMessagingService.class);
        context.stopService(intent);
    }

    private Handler uiThreadHandler = null;
    private PowerManager.WakeLock wakeLock = null;
    private BroadcastReceiver pebbleKitReceiver = null;
    @Override
    public void onCreate()
    {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(Service.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"SomethingLibertyWakeLock");
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("SomethingLiberty","GameMessagingService Started");
        uiThreadHandler = new Handler();
        pebbleKitReceiver = PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID)
        {
            @Override
            public void receiveData(Context context, int transactionId, PebbleDictionary data)
            {
                String messageType = data.getString(PEBBLE_KEY_ATTACK);
                if(PEBBLE_MESSAGE_TYPE_ATTACK.equals(messageType))
                {
                    SendMessage.sendAttackMessage(context);
                    PebbleKit.sendAckToPebble(context,transactionId);
                }
            }
        });

        final MessagingUtils.ConnectionLostHandler thisConnectionLostHandler = this;
        final MessagingUtils.NewGameMessageHandler thisMessageHandler = this;
        final String username = UserUtils.getUsername(this);
        new Thread(new Runnable(){
            @Override
            public void run() {

                MessagingUtils messagingUtils = MessagingUtils.getMessagingUtils(UserUtils.getUsername(GameMessagingService.this));
                messagingUtils.subscribeToTopic(MQTT_TOPIC_SPLATTED + username,thisMessageHandler);
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
        return null;
    }

    @Override
    public void onNewGameMessage(final String topic, final MqttMessage message)
    {
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                String messageString = new String(message.getPayload());
                Log.i("SomethingLiberty", "GameMessagingService : received : " + messageString);

                if(topic.contains(MQTT_TOPIC_SPLATTED))
                {
                    handleSplattedMessage(message);
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
                Toast.makeText(thisService,"Lost MQTT connection",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSplattedMessage(MqttMessage splattedMessage)
    {
        String payloadString = new String(splattedMessage.getPayload());
        String messageToDisplay = null;
        try
        {
            JSONObject payloadObject = null;
            payloadObject = new JSONObject(payloadString);
            messageToDisplay = payloadObject.getString(GameMessageReceiver.EXTRA_MESSAGE);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("SomethingLiberty","Failed to parse splatted message");
            return;
        }

        Intent broadcastMessageIntent = new Intent();
        broadcastMessageIntent.setAction(GameMessageReceiver.ACTION_HANDLE_SPLATTED_MESSAGE);
        broadcastMessageIntent.putExtra(GameMessageReceiver.EXTRA_MESSAGE,messageToDisplay);
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
            responseResult = payloadObject.getString(GameMessageReceiver.EXTRA_RESPONSE_TYPE);
            messageToDisplay = payloadObject.getString(GameMessageReceiver.EXTRA_ATTACKER_MESSAGE);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("SomethingLiberty","Failed to parse attack response message");
            return;
        }

        Intent broadcastMessageIntent = new Intent();
        broadcastMessageIntent.setAction(GameMessageReceiver.ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE);
        broadcastMessageIntent.putExtra(GameMessageReceiver.EXTRA_RESPONSE_TYPE, responseResult);
        broadcastMessageIntent.putExtra(GameMessageReceiver.EXTRA_ATTACKER_MESSAGE,messageToDisplay);
        sendOrderedBroadcast(broadcastMessageIntent, null);
    }

    private void handleNewsMessage(MqttMessage newsMessage)
    {
        Intent broadcastMessageIntent = new Intent();
        broadcastMessageIntent.setAction(GameMessageReceiver.ACTION_HANDLE_NEWS_MESSAGE);
        broadcastMessageIntent.putExtra(GameMessageReceiver.EXTRA_NEWS_JSON, new String(newsMessage.getPayload()));
        sendOrderedBroadcast(broadcastMessageIntent, null);
    }

    private void handleOutgunnerMessage(MqttMessage outgunnerMessage)
    {
        String messageString = null;
        try
        {
            JSONObject payloadObject = new JSONObject(new String(outgunnerMessage.getPayload()));
            messageString = payloadObject.getString(GameMessageReceiver.EXTRA_MESSAGE);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("SomethingLiberty","Failed to parse outgunner message");
            return;
        }

        Intent broadcastMessageIntent = new Intent();
        broadcastMessageIntent.putExtra(GameMessageReceiver.EXTRA_MESSAGE,messageString);
        broadcastMessageIntent.setAction(GameMessageReceiver.ACTION_HANDLE_OUTGUNNER_MESSAGE);
        sendOrderedBroadcast(broadcastMessageIntent,null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(pebbleKitReceiver != null)
        {
            unregisterReceiver(pebbleKitReceiver);
        }
        wakeLock.release();
    }
}
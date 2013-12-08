package com.something.liberty.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class GameMessagingService extends Service implements NewGameMessageHandler, ConnectionLostHandler
{
    private Handler uiThreadHandler = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"Service started",Toast.LENGTH_SHORT).show();
        uiThreadHandler = new Handler();

        final ConnectionLostHandler thisConnectionLostHandler = this;
        final NewGameMessageHandler thisMessageHandler = this;
        PowerManager powerManager = (PowerManager) getSystemService(Service.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"SomethingLibertyWakeLock");
        wakeLock.acquire();
        new Thread(new Runnable(){
            @Override
            public void run() {

                MessagingUtils messagingUtils = MessagingUtils.getMessagingUtils();
                messagingUtils.subscribeToTopic("something/killed/alex_pringle",thisMessageHandler);
                messagingUtils.subscribeToTopic("something/attResponse/alex_pringle",thisMessageHandler);
                messagingUtils.setConnectionLostHandler(thisConnectionLostHandler);
            }
        }).run();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onNewGameMessage(final String topic, final MqttMessage message) {
        final Service thisService = this;
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                String messageString = new String(message.getPayload());
                Log.i("SomethingLiberty", "GameMessagingService : received : " + messageString);
                Toast.makeText(thisService,messageString,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionLost(final Throwable cause) {
        final Service thisService = this;
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("SomethingLiberty", "GameMessagingService : Client disconnected : " + cause.getMessage());
                Toast.makeText(thisService,"Lost Mqtt connection",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

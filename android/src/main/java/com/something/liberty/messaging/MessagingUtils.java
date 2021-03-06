package com.something.liberty.messaging;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Pringle
 */
class MessagingUtils implements MqttCallback
{
    private static final String MQTT_BROKER_URL = BrokerDetails.MQTT_BROKER_URL;

    private static MessagingUtils mMessagingUtils = null;

    public static synchronized MessagingUtils getMessagingUtils(String mqttClientId)
    {
        if(mMessagingUtils == null)
        {
            try
            {
                mMessagingUtils = new MessagingUtils(mqttClientId);
            }
            catch(MqttException e)
            {
                Log.e("SomethingLiberty","Failed to instantiate MessagingUtils");
                e.printStackTrace();
            }
        }
        return mMessagingUtils;
    }

    private MqttClient mMqttClient = null;
    private ConnectionLostHandler mConnectionLostHandler = null;
    private Map<String,NewGameMessageHandler> topicHandlerMap = new HashMap<String, NewGameMessageHandler>();
    private String mqttClientId = null;

    private MessagingUtils(String clientId) throws MqttException
    {
        this.mqttClientId = clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        mMqttClient = new MqttClient(MQTT_BROKER_URL,mqttClientId,persistence);
    }

    public synchronized void connectToBroker() throws MqttException
    {
        if(!mMqttClient.isConnected())
        {
            mMqttClient.connect();
            mMqttClient.setCallback(this);
            Log.i("SomethingLiberty","Connected to broker : " + MQTT_BROKER_URL);
        }
    }

    public void sendMessage(String topicString, String messageString)
    {
        if(!mMqttClient.isConnected())
        {
            try
            {
                connectToBroker();
            }
            catch(MqttException e)
            {
                Log.e("SomethingLiberty","Failed to connect to broker whilst publishing : " + messageString + "  to topic : " + topicString);
                return;
            }
        }

        MqttMessage message = new MqttMessage();
        message.setPayload(messageString.getBytes());
        try
        {
            mMqttClient.publish(topicString,message);
        }
        catch(MqttException e)
        {
            Log.e("SomethingLiberty","Failed to send message string : " + messageString + " to topic " + topicString);
            e.printStackTrace();
            return;
        }

        Log.i("SomethingLiberty","Sent message : " + messageString + " to topic : " + topicString);
    }

    public void subscribeToTopic(String topic,NewGameMessageHandler callback)
    {
        if(!mMqttClient.isConnected())
        {
            try
            {
                connectToBroker();
            }
            catch(MqttException e)
            {
                Log.e("SomethingLiberty","Failed to connect to broker whilst subscribing to topic : " + topic);
                return;
            }
        }

        try
        {
            mMqttClient.subscribe(topic);
        }
        catch(MqttException e)
        {
            Log.e("SomethingLiberty","Failed to subscribe to topic : " + topic);
            e.printStackTrace();
            return;
        }
        Log.i("SomethingLiberty","Subscribed to topic : " + topic);

        topicHandlerMap.put(topic, callback);
    }

    @Override
    public void connectionLost(Throwable throwable)
    {
        if(mConnectionLostHandler != null)
        {
            mConnectionLostHandler.onConnectionLost(throwable);
        }
        Log.i("SomethingLiberty","Lost connection to broker : " + throwable.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage)
    {
        Log.i("SomethingLiberty","Received message : " + new String(mqttMessage.getPayload()) + " on topic " + topic);

        NewGameMessageHandler handlerToEnvoke = topicHandlerMap.get(topic);
        if(handlerToEnvoke != null)
        {
            Log.i("SomethingLiberty","Handling message");
            handlerToEnvoke.onNewGameMessage(topic, mqttMessage);
        }
        else
        {
            Log.i("SomethingLiberty","Not handling message");
        }
     }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
    {
        try
        {
            Log.i("SomethingLiberty","Delivery complete : " + iMqttDeliveryToken.getMessage());
        }
        catch (MqttException e)
        {
            Log.e("SomethingLiberty","Failed to read IMqttDeliveryToken");
            e.printStackTrace();
        }
    }

    public void setConnectionLostHandler(ConnectionLostHandler handler)
    {
        mConnectionLostHandler = handler;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException("Singleton Instance");
    }

    static interface NewGameMessageHandler
    {
        void onNewGameMessage(String topic, MqttMessage message);
    }

    static interface ConnectionLostHandler
    {
        void onConnectionLost(Throwable throwable);
    }
}

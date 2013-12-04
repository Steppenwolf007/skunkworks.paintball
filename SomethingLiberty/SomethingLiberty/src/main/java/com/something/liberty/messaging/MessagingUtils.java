package com.something.liberty.messaging;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

class MessagingUtils implements MqttCallback
{
    private static final String MQTT_CLIENT_ID = "SomethingClient";
    private static final String MQTT_BROKER_URL = BrokerDetails.MQTT_BROKER_URL;

    private static MessagingUtils mMessagingUtils = null;

    public static MessagingUtils getMessagingUtils()
    {
        if(mMessagingUtils == null)
        {
            try
            {
                mMessagingUtils = new MessagingUtils();
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

    private MessagingUtils() throws MqttException
    {
        MemoryPersistence persistence = new MemoryPersistence();
        mMqttClient = new MqttClient(MQTT_BROKER_URL,MQTT_CLIENT_ID,persistence);
    }

    public void sendMesage(String topicString,String messageString)
    {
        if(!mMqttClient.isConnected())
        {
            try
            {
                mMqttClient.connect();
            }
            catch(MqttException e)
            {
                Log.e("SomethingLiberty","Failed to connect to broker whilst sending message : "
                        + messageString + " to topic : " + topicString);
                e.printStackTrace();
                return;
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
    }

    @Override
    public void connectionLost(Throwable throwable)
    {
        Log.i("SomethingLiberty","Lost connection to broker : " + throwable.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage)
    {
        Log.i("SomethingLiberty","Recieved message : " + mqttMessage.getPayload() + " on topic " + topic);
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

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException("Singleton Instance");
    }
}

package com.something.liberty.messaging;

import org.eclipse.paho.client.mqttv3.MqttMessage;

interface NewGameMessageHandler
{
    void onNewGameMessage(String topic, MqttMessage message);
}

package com.something.liberty.messaging;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

class SendJsonToTopicTask extends AsyncTask<JSONObject,Void,Void> {

    private String mTopic = null;

    public SendJsonToTopicTask(String topic)
    {
        mTopic = topic;
    }

    @Override
    protected Void doInBackground(JSONObject... params)
    {
        JSONObject jsonMessage = params[0];
        if(jsonMessage == null)
        {
            Log.e("SomethingLiberty","SendJsonToTopicTask : invalid json object " + jsonMessage);
            return null;
        }
        String jsonString = jsonMessage.toString();
        Log.d("SomethingLiberty","jsonString = " + jsonString);
        MessagingUtils messagingUtils = MessagingUtils.getMessagingUtils();
        messagingUtils.sendMessage(mTopic, jsonString);
        return null;
    }
}
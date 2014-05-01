package com.something.liberty.messaging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.something.liberty.UserUtils;

import org.json.JSONObject;

/**
 * @author Alexander Pringle
 */
class SendJsonToTopicTask extends AsyncTask<JSONObject,Void,Void> {

    private String mTopic = null;
    private Context mContext = null;
    public SendJsonToTopicTask(Context context,String topic)
    {
        mContext = context;
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
        MessagingUtils messagingUtils = MessagingUtils.getMessagingUtils(UserUtils.getUsername(mContext));
        messagingUtils.sendMessage(mTopic, jsonString);
        return null;
    }
}
package com.something.liberty;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.something.liberty.location.ReportLocationService;
import com.something.liberty.messaging.GameMessageReceiver;
import com.something.liberty.messaging.GameMessagingService;
import com.something.liberty.messaging.SendMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity
{
    private static final int LOCATION_POLL_INTERVAL = 30000;


    private BroadcastReceiver gameMessageBroadcastReceiver = null;
    private IntentFilter gameMessageIntentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserUtils.updateUsernameIfUnset(this);

        // start messaging service
        Intent intent = new Intent(this,GameMessagingService.class);
        startService(intent);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,new Intent(this,ReportLocationService.class),0);
        //cancel existing alarm if one exists
        alarmManager.cancel(pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + LOCATION_POLL_INTERVAL, LOCATION_POLL_INTERVAL, pendingIntent);

        setupMapWebView();

        setupGameMessageReceiver();
    }

    private void setupMapWebView()
    {
        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webView.loadUrl("file:///android_asset/index.html");
        webView.addJavascriptInterface(this, "Android");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_attack:
                SendMessage.sendAttackMessage(this);
                break;

            case R.id.menu_news:
                SendMessage.sendNewsRequest(this);
                break;

            case R.id.menu_news_page:
                Intent intent = new Intent(this,NewsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this,SettingsActivity.class);
                startActivity(settingsIntent);
                break;

        }
        return true;
    }

    private void removeAllEventsFromMap()
    {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("javascript:removeAllEvents()");
    }

    private void addEventToMap(String iconSrc, double longitude, double latitude)
    {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("javascript:addEvent('" + iconSrc + "'," + longitude + "," + latitude + ")");
    }

    private void setupGameMessageReceiver()
    {
        gameMessageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(GameMessageReceiver.ACTION_HANDLE_KILLED_MESSAGE.equals(intent.getAction()))
                {
                    String title = context.getString(R.string.splatted);
                    String message = intent.getStringExtra(GameMessageReceiver.EXTRA_MESSAGE);
                    showMessageDialog(title,message);
                }
                else if(GameMessageReceiver.ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE.equals(intent.getAction()))
                {
                    String title = intent.getStringExtra(GameMessageReceiver.EXTRA_RESPONSE_TYPE);
                    String message = intent.getStringExtra(GameMessageReceiver.EXTRA_ATTACKER_MESSAGE);
                    showMessageDialog(title,message);
                }
                else if(GameMessageReceiver.ACTION_HANDLE_NEWS_MESSAGE.equals(intent.getAction()))
                {
                    String newsJson = intent.getStringExtra(GameMessageReceiver.EXTRA_NEWS_JSON);
                    parseAndDisplayNews(newsJson);
                }
                else if(GameMessageReceiver.ACTION_HANDLE_OUTGUNNER_MESSAGE.equals(intent.getAction()))
                {
                    String message = intent.getStringExtra(GameMessageReceiver.EXTRA_MESSAGE);
                    showMessageDialog(context.getString(R.string.self_defence),message);
                }
                abortBroadcast();
            }
        };
        gameMessageIntentFilter = new IntentFilter();
        gameMessageIntentFilter.addAction(GameMessageReceiver.ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE);
        gameMessageIntentFilter.addAction(GameMessageReceiver.ACTION_HANDLE_KILLED_MESSAGE);
        gameMessageIntentFilter.addAction(GameMessageReceiver.ACTION_HANDLE_NEWS_MESSAGE);
        gameMessageIntentFilter.addAction(GameMessageReceiver.ACTION_HANDLE_OUTGUNNER_MESSAGE);
        gameMessageIntentFilter.setPriority(10);
    }


    private void showMessageDialog(String title,String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void parseAndDisplayNews(String news)
    {
        removeAllEventsFromMap();
        JSONArray newsSummaries = new JSONArray();
        try
        {
            JSONArray newsArray = new JSONArray(news);
            for(int i=0; i < newsArray.length();i++)
            {
                JSONObject currentNewsItem = (JSONObject) newsArray.get(i);

                JSONObject location = currentNewsItem.getJSONObject("location");
                double longitude = location.getDouble("longitude");
                double latitude = location.getDouble("latitude");

                String eventType = currentNewsItem.getString("type");
                JSONObject attackerIdentity = currentNewsItem.getJSONObject("attackerIdentity");
                String attackerGeneratedName = attackerIdentity.getString("generatedName");
                String eventColor = attackerIdentity.getString("colorAsHexString");

                long timeMilis = currentNewsItem.getLong("timeOccurred");
                Date date = new Date(timeMilis);

                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String whatHappened = "";
                
                if(eventType.equals(MapIconUtils.NEWS_TYPE_OUTGUNNED))
                {
                    whatHappened = "Was Outgunned";
                }
                else if(eventType.equals(MapIconUtils.NEWS_TYPE_MISS))
                {
                    whatHappened = "Missed";
                }
                else if(eventType.equals(MapIconUtils.NEWS_TYPE_HIT))
                {
                    whatHappened = "Splatted";
                }

                String message = whatHappened + ": " +attackerGeneratedName + "\n" + dateFormat.format(date);
                newsSummaries.put(message);
                String iconSrc = MapIconUtils.getIconSrc(eventType,eventColor);
                addEventToMap(iconSrc, longitude, latitude);
            }
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPrefs.edit().putString(NewsActivity.SHARED_PREF_NEWS,newsSummaries.toString()).commit();
            Log.d("SomethingLiberty",newsSummaries.toString());
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("SomethingLiberty","Failed to parse news");
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(gameMessageBroadcastReceiver);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(gameMessageBroadcastReceiver,gameMessageIntentFilter);
        SendMessage.sendNewsRequest(this);
    }
}

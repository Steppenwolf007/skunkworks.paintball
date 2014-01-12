package com.something.liberty;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.something.liberty.location.LocationUtils;
import com.something.liberty.location.ReportLocationService;
import com.something.liberty.messaging.GameMessageReceiver;
import com.something.liberty.messaging.GameMessagingService;
import com.something.liberty.messaging.SendMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity {

    public static final String ACTION_HANDLE_NEWS_MESSAGE = "HANDLE_NEWS_MESSAGE";

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

        // test location polling
        AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,new Intent(this,ReportLocationService.class),0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 30000, 120000, pendingIntent);

        //setup map web view
        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webView.loadUrl("file:///android_asset/index.html");
        webView.addJavascriptInterface(this, "Android");

        setupGameMessageReciever();
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
                LocationUtils locationUtils = new LocationUtils(this);
                Location lastKnownLocation = locationUtils.getLastKnownLocation();
                if(lastKnownLocation != null)
                {
                    SendMessage.sendAttackMessage(this,lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude());
                }
                else
                {
                    Toast.makeText(this,getResources().getString(R.string.no_known_location),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_settings:

            return true;
        }
        return super.onOptionsItemSelected(item);

    private void removeAllEventsFromMap()
    {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("javascript:removeAllEvents()");
    }

    private void addEventToMap(String message, double longitude, double latitude)
    {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("javascript:addEvent('" + message + "'," + longitude + "," + latitude + ")");
    }

    private void setupGameMessageReciever()
    {
        gameMessageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(GameMessageReceiver.ACTION_HANDLE_KILLED_MESSAGE.equals(intent.getAction()))
                {
                    String title = "Killed";
                    String message = intent.getStringExtra("message");
                    showMessageDialog(title,message);
                }
                else if(GameMessageReceiver.ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE.equals(intent.getAction()))
                {
                    String title = intent.getStringExtra("responseType");
                    String message = intent.getStringExtra("attackerMessage");
                    showMessageDialog(title,message);
                }
                else if(ACTION_HANDLE_NEWS_MESSAGE.equals(intent.getAction()))
                {
                    String news = intent.getStringExtra("news");
                    displayNewsOnMap(news);
                }
                else if(GameMessageReceiver.ACTION_HANDLE_OUTGUNNER_MESSAGE.equals(intent.getAction()))
                {
                    String message = intent.getStringExtra("message");
                    showMessageDialog("Self Defence",message);
                }
                abortBroadcast();
            }
        };
        gameMessageIntentFilter = new IntentFilter();
        gameMessageIntentFilter.addAction(GameMessageReceiver.ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE);
        gameMessageIntentFilter.addAction(GameMessageReceiver.ACTION_HANDLE_KILLED_MESSAGE);
        gameMessageIntentFilter.addAction(ACTION_HANDLE_NEWS_MESSAGE);
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

    private void displayNewsOnMap(String news)
    {
        removeAllEventsFromMap();
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
                String attackerUsername = currentNewsItem.getString("attackerUsername");

                long timeMilis = currentNewsItem.getLong("timeOccurred");
                Date date = new Date(timeMilis);

                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String whatHappened = "";
                
                if(eventType.equals("PLAYER_OUTGUNNED"))
                {
                    whatHappened = "Was Outgunned Here";
                }
                else if(eventType.equals("PLAYER_MISS"))
                {
                    whatHappened = "Missed Here";
                }
                else if(eventType.equals("PLAYER_HIT"))
                {
                    whatHappened = "Killed Here";
                }

                String message = attackerUsername + " " + whatHappened + " at " + dateFormat.format(date);
                addEventToMap(message, longitude, latitude);
            }
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

    AlertDialog currentlyDisplayedEvent = null;
    @JavascriptInterface
    public void showEventDialog(String message)
    {

        if(currentlyDisplayedEvent == null || !currentlyDisplayedEvent.isShowing())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Event");
            builder.setMessage(message);
            currentlyDisplayedEvent = builder.show();
        }
    }

}

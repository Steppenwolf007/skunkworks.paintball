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
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.something.liberty.location.LocationUtils;
import com.something.liberty.location.ReportLocationService;
import com.something.liberty.messaging.GameMessageReciever;
import com.something.liberty.messaging.GameMessagingService;
import com.something.liberty.messaging.SendMessage;

public class MainActivity extends ActionBarActivity {

    private BroadcastReceiver gameMessageBroadcastReceiver = null;
    private IntentFilter gameMessageIntentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        setupGameMessageReciever();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_attack:
                LocationUtils locationUtils = new LocationUtils(this);
                Location lastKnownLocation = locationUtils.getLastKnownLocation();
                SendMessage.sendAttackMessage(this,lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude());
                break;
            case R.id.action_settings:

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addMarker(double longitude,double latitude)
    {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("javascript:displayEvent(" + longitude + "," + latitude + ")");
    }

    private void setupGameMessageReciever()
    {
        gameMessageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(GameMessageReciever.ACTION_HANDLE_KILLED_MESSAGE.equals(intent.getAction()))
                {
                    String title = "Killed";
                    String message = intent.getStringExtra("message");
                    showMessageDialog(title,message);
                }
                else if(GameMessageReciever.ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE.equals(intent.getAction()))
                {
                    String title = intent.getStringExtra("responseType");
                    String message = intent.getStringExtra("attackerMessage");
                    showMessageDialog(title,message);
                }
                abortBroadcast();
            }
        };
        gameMessageIntentFilter = new IntentFilter();
        gameMessageIntentFilter.addAction(GameMessageReciever.ACTION_HANDLE_ATTACK_RESPONSE_MESSAGE);
        gameMessageIntentFilter.addAction(GameMessageReciever.ACTION_HANDLE_KILLED_MESSAGE);
        gameMessageIntentFilter.setPriority(10);
    }

    private void showMessageDialog(String title,String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gameMessageBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gameMessageBroadcastReceiver,gameMessageIntentFilter);
    }


}

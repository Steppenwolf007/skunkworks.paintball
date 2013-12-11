package com.something.liberty.location;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.something.liberty.UserUtils;
import com.something.liberty.messaging.GameMessagingService;
import com.something.liberty.messaging.SendMessage;

public class ReportLocationService extends Service {

    public ReportLocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        reportLocation();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        reportLocation();
        return null;
    }

    private void reportLocation()
    {
        Log.i("SomethingLiberty", "Requesting location update from device");

        LocationManager locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("SomethingLiberty","Received location update from device");
                SendMessage.sendLocationUpdate(getApplicationContext(),location.getLongitude(),location.getLatitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        },null);
    }

}

package com.something.liberty.location;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtils
{
    private Context mContext = null;

    public LocationUtils(Context context)
    {
        this.mContext = context;
    }

    public Location getLastKnownLocation()
    {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Service.LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
}

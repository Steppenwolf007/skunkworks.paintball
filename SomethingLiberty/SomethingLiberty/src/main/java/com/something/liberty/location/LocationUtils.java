package com.something.liberty.location;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * @author Alexander Pringle
 */
public abstract class LocationUtils
{

    public static Location getLastKnownLocation(Context context)
    {
        LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
}

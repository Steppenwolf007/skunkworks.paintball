package com.something.liberty.alerts;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.something.liberty.R;

/**
 * @author Alexander Pringle
 */
public class NotificationUtils
{
    private Context mContext = null;

    public NotificationUtils(Context context)
    {
        mContext = context;
    }

    public void displayGenericNotification(String titleString,String contentString)
    {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(titleString);
        builder.setContentText(contentString);
        builder.setTicker(mContext.getString(R.string.notification_ticker));

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(notificationSound);

        builder.setVibrate(new long[]{100,100,100,100});

        notificationManager.notify(contentString.hashCode(),builder.build());
    }

}

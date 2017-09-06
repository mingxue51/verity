package com.protovate.verity.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.events.NotificationEvent;
import com.protovate.verity.ui.MainActivity;

/**
 * Created by Yan on 8/1/15.
 */
public class GCMIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GCMIntentService() {
        super("GCMIntent service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("message"));

                BusProvider.getInstance().post(new NotificationEvent(true));
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String title) {
        if (!TextUtils.isEmpty(title)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("notifications", true);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("Verity")
                    .setContentText(title)
                    .setTicker(title)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
}

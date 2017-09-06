package com.protovate.verity.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.events.Unlock;
import com.protovate.verity.ui.MainActivity;

import javax.inject.Inject;

/**
 * Created by Yan on 8/18/15.
 */
public class GeofenceTransitionsIntentService extends IntentService {
    protected static final String TAG = "geofence-transitions-service";
    private static final int NOTIFICATION_ID = 2;
    @Inject Profile profile;

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplication()).component().inject(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            System.out.println("geofencingEvent.getErrorCode() = " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // show notification
            profile.setUnlock(true);
            BusProvider.getInstance().post(new Unlock());
            if (profile.getLatestLockId() != 0) {
                sendNotification("V-Lock activity has been unlocked", "You have left your initial location");
            }
        }
    }

    private void sendNotification(String title, String subtitle) {
        if (!TextUtils.isEmpty(title)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("Verity")
                    .setContentText(title)
                    .setSubText(subtitle)
                    .setTicker(title)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            stopSelf();
        }
    }

    private String getGeofenceTransitionDetails(int geofenceTransition) {

        return getTransitionString(geofenceTransition);
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "ENTERED GEOFENCE";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "EXITED GEOFENCE";
            default:
                return "UNKNOWN GEOFENCE TRANSITION";
        }
    }
}

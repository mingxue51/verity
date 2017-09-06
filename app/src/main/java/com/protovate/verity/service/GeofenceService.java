package com.protovate.verity.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Yan on 8/19/15.
 */
public class GeofenceService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    protected static final String TAG = "creating-and-monitoring-geofences";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    private double mLatitude = 0, mLongitude = 0;

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!mGoogleApiClient.isConnected()) {

        if (intent != null && intent.getDoubleExtra("latitude", 0) != 0) {
            try {
                double latitude = intent.getDoubleExtra("latitude", 0);
                double longitude = intent.getDoubleExtra("longitude", 0);

                if (latitude != 0 && longitude != 0) {
                    mLatitude = latitude;
                    mLongitude = longitude;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mGeofencePendingIntent = null;
//        }
            mGoogleApiClient.connect();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        buildGoogleApiClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }


    @Override public void onConnected(Bundle bundle) {
        System.out.println("Connected to Google API Client");
        generateGeofence();
        addGeofences();
    }

    @Override public void onConnectionSuspended(int i) {
        System.out.println("Connection suspended");
    }

    @Override public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("connectionResult = " + connectionResult);
    }

    @Override public void onResult(Status status) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public Geofence generateGeofence() {
        return new Geofence.Builder()
                .setRequestId("Workplace")
                .setCircularRegion(mLatitude, mLongitude, 200)
                .setExpirationDuration(172800000) //2 days
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofence(generateGeofence());
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}

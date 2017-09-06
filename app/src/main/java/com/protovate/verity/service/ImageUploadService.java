package com.protovate.verity.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.data.PhotoItem;
import com.protovate.verity.data.PhotosSelected;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.events.TypeEvent;
import com.protovate.verity.data.responses.Success;

import org.parceler.Parcels;

import java.io.File;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Yan on 7/11/15.
 */
public class ImageUploadService extends Service {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    private int countSize = 0;
    private int count = 1;
    private PhotosSelected photos;
    private NotificationManager mNotifManager;

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        ((App) getApplication()).component().inject(this);

        mNotifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        uploadFiles(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadFiles(Intent intent) {
        try {
            photos = Parcels.unwrap(intent.getParcelableExtra("photos"));
            countSize += photos.getPhotos().size();

            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setTicker("Started uploading photos")
                    .setContentTitle("Verity")
                    .setContentText("Uploading photos: " + count + "/" + countSize)
                    .setProgress(0, 0, true)
                    .setOngoing(true);

            mNotifManager.notify(1, notification.build());

            for (PhotoItem photo : photos.getPhotos()) {
                //count += 1;
                //photos.getPhotos().remove(photo);

                Uri uri = photo.getFullImageUri();
                File file;

                if (uri.toString().contains("file:/"))
                    file = new File(uri.getPath());
                else
                    file = new File(uri.toString());

                apiClient.createVlockPhoto(String.valueOf(profile.getLatestLockId()), new TypedFile("image/*", file), profile.getAccessToken(), new Callback<Success>() {
                    @Override public void success(Success success, Response response) {
                        if (success.isSuccess()) {
                            if (count == countSize) {
                                NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                                        .setTicker("Photo upload finished")
                                        .setContentTitle("Verity")
                                        .setContentText("Uploaded photo: " + count + "/" + countSize)
                                        .setAutoCancel(true);
                                mNotifManager.notify(1, notif.build());
                                BusProvider.getInstance().post(new TypeEvent());
                                stopSelf();
                            } else {
                                count++;
                                NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(android.R.drawable.stat_sys_upload)
                                        .setTicker("Started uploading photos")
                                        .setContentTitle("Verity")
                                        .setContentText("Uploading photo: " + count + "/" + countSize)
                                        .setProgress(0, 0, true)
                                        .setOngoing(true);

                                mNotifManager.notify(1, notif.build());
                            }
                        } else {
                            for (com.protovate.verity.data.responses.Error error : success.getErrors()) {
                                Toast.makeText(ImageUploadService.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override public void failure(RetrofitError error) {
                        error.printStackTrace();
                        NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                                .setTicker("Error")
                                .setContentTitle("Verity")
                                .setContentText("Error while trying to upload photo")
                                .setAutoCancel(true);
                        mNotifManager.notify(1, notif.build());
                        stopSelf();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroyed = ");
    }
}

package com.protovate.verity.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.VideoItem;
import com.protovate.verity.data.VideoSelected;
import com.protovate.verity.data.events.TypeEvent;
import com.protovate.verity.data.responses.Success;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Yan on 7/11/15.
 */
public class VideoUploadService extends Service {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    private int countSize;
    private int count = 1;
    private ArrayList<VideoItem> videos = new ArrayList<>();
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
            videos.addAll(((VideoSelected) Parcels.unwrap(intent.getParcelableExtra("videos"))).getVideos());
            countSize += videos.size();

            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setTicker("Started uploading videos")
                    .setContentTitle("Verity")
                    .setContentText("Uploading videos: " + count + "/" + countSize)
                    .setProgress(0, 0, true)
                    .setOngoing(true);

            mNotifManager.notify(1, notification.build());

            for (VideoItem video : videos) {
                System.out.println("video = " + video.getPath());
                apiClient.createVlockVideo(
                        String.valueOf(profile.getLatestLockId()),
                        new TypedFile("video/*", new File(video.getPath())),
                        new TypedFile("image/*", new File(video.getThumb())),
                        profile.getAccessToken(),
                        new Callback<Success>() {
                            @Override public void success(Success success, Response response) {
                                if (success.isSuccess()) {
                                    if (count == countSize) {
                                        NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext())
                                                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                                                .setTicker("Video upload finished")
                                                .setContentTitle("Verity")
                                                .setContentText("Uploaded videos: " + count + "/" + countSize)
                                                .setAutoCancel(true);
                                        mNotifManager.notify(1, notif.build());
                                        BusProvider.getInstance().post(new TypeEvent());
                                        stopSelf();
                                    } else {
                                        count += 1;
                                        NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext())
                                                .setSmallIcon(android.R.drawable.stat_sys_upload)
                                                .setTicker("Started uploading videos")
                                                .setContentTitle("Verity")
                                                .setContentText("Uploading video: " + count + "/" + countSize)
                                                .setProgress(0, 0, true)
                                                .setOngoing(true);

                                        mNotifManager.notify(1, notif.build());
                                    }
                                } else {
                                    for (com.protovate.verity.data.responses.Error error : success.getErrors()) {
                                        Toast.makeText(VideoUploadService.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override public void failure(RetrofitError error) {
                                error.printStackTrace();
                                NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                                        .setTicker("Error")
                                        .setContentTitle("Verity")
                                        .setContentText("Error while trying to upload video")
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


}

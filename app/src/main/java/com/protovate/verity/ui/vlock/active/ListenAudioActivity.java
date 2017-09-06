package com.protovate.verity.ui.vlock.active;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.Vlock;
import com.protovate.verity.data.events.TypeEvent;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.MainActivity;
import com.protovate.verity.utils.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * Created by Yan on 7/2/15.
 */
public class ListenAudioActivity extends BaseActivity {
    @Inject ApiClient apiClient;
    @Inject Profile profile;
    @InjectView(R.id.duration) TextView mDuration;
    @InjectView(R.id.btnPreview) ImageView mBtnPreview;

    private Vlock vlock;
    private MediaPlayer mediaPlayer;
    private ProgressDialog dialog;
    int duration;
    private Handler timerHandler;
    private Runnable timerRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_audio);

        ((App) getApplication()).component().inject(this);

        ButterKnife.inject(this);

        dialog = new ProgressDialog(this, R.style.DialogTheme);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Record Audio Note");
        vlock = (Vlock) getIntent().getSerializableExtra("vlock");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> mBtnPreview.setBackgroundResource(R.drawable.play_btn_with_text));

        try {
            mediaPlayer.setDataSource(vlock.getVoiceFile().getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDuration();
    }

    private void setDuration() {
        int minute = 0;
        int secs = getIntent().getIntExtra("duration", 0);
        if (secs > 59) {
            minute = secs / 60;
            secs = secs - minute * 60;
        }
        mDuration.setText(String.format("%02d:%02d", minute, secs));
    }

    @OnClick(R.id.btnPreview) void preview() {
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.setDataSource(vlock.getVoiceFile().getPath());
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mediaPlayer.start();

            /*ticks(mediaPlayer).takeUntil(complete(mediaPlayer))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(seconds -> {
                        System.out.println("seconds = " + seconds);
                        int minute = 0;
                        int secs = seconds / 1000;
                        if (secs > 59) {
                            minute = secs / 60;
                            secs = secs - minute * 60;
                        }

                        mDuration.setText(String.format("%02d:%02d", minute, secs));
                    }); */

            timerRunnable = () -> {
                int minute = 0;
                int secs = mediaPlayer.getCurrentPosition() / 1000;
                if (secs > 59) {
                    minute = secs / 60;
                    secs = secs - minute * 60;
                }
                mDuration.setText(String.format("%02d:%02d", minute, secs));
                timerHandler.postDelayed(timerRunnable, 100);
            };
            timerHandler = new Handler();
            timerHandler.post(timerRunnable);
            mBtnPreview.setBackgroundResource(R.drawable.pause_btn_with_text);

            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> resetCounter());
        } else {
            resetCounter();

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
        }
    }

    private void resetCounter() {
        timerHandler.removeCallbacks(timerRunnable);
        setDuration();
        mBtnPreview.setBackgroundResource(R.drawable.play_btn_with_text);
    }

    Observable<Integer> ticks(MediaPlayer mp) {
        return Observable.interval(16, TimeUnit.MILLISECONDS)
                .map(y -> getCurrentPosition(mp));
    }

    Observable<MediaPlayer> complete(MediaPlayer player) {
        return Observable.create(subscriber -> player.setOnCompletionListener(mp -> {
            mBtnPreview.setBackgroundResource(R.drawable.play_btn_with_text);
            subscriber.onNext(mp);
            subscriber.onCompleted();
        }));
    }

    public int getCurrentPosition(MediaPlayer mp) {
        try {
            return mp.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    @OnClick(R.id.btnRecordAgain) void recordAgain() {
        finish();
    }

    @OnClick(R.id.btnSaveAndRecordAgain) void saveAndRecordAgain() {
        dialog.show();
        apiClient.createVlockAudio(String.valueOf(profile.getLatestLockId()), new TypedFile("audio/mp3", vlock.getVoiceFile()), profile.getAccessToken(), new Callback<User>() {
            @Override public void success(User user, Response response) {
                dialog.dismiss();
                if (user.isSuccess()) {
                    finish();
                } else {
                    for (User.Error error : user.getErrors()) {
                        Toast.makeText(ListenAudioActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override public void failure(RetrofitError error) {

            }
        });
    }

    @OnClick(R.id.btnSave) void save() {
        if (Utils.isNetworkAvailable(this)) {
            dialog.show();
            apiClient.createVlockAudio(String.valueOf(profile.getLatestLockId()), new TypedFile("audio/mp3", vlock.getVoiceFile()), profile.getAccessToken(), new Callback<User>() {
                @Override public void success(User user, Response response) {
                    dialog.dismiss();
                    if (user.isSuccess()) {
                        BusProvider.getInstance().post(new TypeEvent());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        for (User.Error error : user.getErrors()) {
                            Toast.makeText(ListenAudioActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override public void failure(RetrofitError error) {
                    dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
}

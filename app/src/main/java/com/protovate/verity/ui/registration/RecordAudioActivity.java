package com.protovate.verity.ui.registration;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.afollestad.Yanrialdialogs.YanrialDialog;
import com.protovate.verity.App;
import com.protovate.verity.BusProvider;
import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.RegisterInfo;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.BaseActivity;
import com.protovate.verity.ui.fragments.dialogs.CongratulationsFragmentDialog;
import com.protovate.verity.ui.views.SoundSurfaceView;
import com.protovate.verity.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 6/17/15.
 */
public class RecordAudioActivity extends BaseActivity {
    @Inject ApiClient apiClient;
    @Inject Profile profile;

    @InjectView(R.id.viewFlipper) ViewFlipper mViewFlipper;
    @InjectViews({
            R.id.one, R.id.two, R.id.three, R.id.four, R.id.five,
            R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.ten
    }) List<TextView> mNumbers;
    @InjectView(R.id.soundSurfaceView) SoundSurfaceView mSoundSurfaceView;
    @InjectView(R.id.tvTimer) TextView mTvTimer;
    @InjectView(R.id.bottomRow) TableRow mBottomRow;
    @InjectView(R.id.recordContainer) LinearLayout mRecordContainer;

    private int timerCount = 0;
    private static int nWord = 0;
    private int maxBufferSize = 8000 * 20;
    public ArrayList<File> audioFileList = new ArrayList<>();
    private RegisterInfo userRegisterInfo;
    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private MediaPlayer mPlayer;
    private MediaRecorder mRecorder;
    private int amplitude = 0;
    private int timePassed = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        userRegisterInfo = (RegisterInfo) getIntent().getSerializableExtra("UserRegisterInfo");

        ((App) getApplication()).component().inject(this);

        ButterKnife.inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Create Account");

        for (TextView tv : mNumbers) {
            tv.setEnabled(false);
        }
    }

    @OnClick(R.id.btnStartRecording) void startRecording() {
        deleteAudioFilesIfExist();
        mViewFlipper.showNext();
        nWord = 0;
        if (mTimerHandler != null) {
            stopTimerHandler();
        }

        startTimerHandler();
        setMediaRecorder();
        //mRecControl.start();
        //setRecordingControl();
    }

    public void setMediaRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(getFilename(nWord));
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            Toast.makeText(this, "There was a problem with recording, please try again.", Toast.LENGTH_SHORT).show();
        }

        final int[] tempTimePassed = {0};
        Observable.interval(50, TimeUnit.MILLISECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(tick -> getCurrentAmplitude())
                .takeWhile(currentAmplitude -> currentAmplitude != -1)
                .subscribe(currentAmplitude -> {
                    if (currentAmplitude != -1) {
                        System.out.println("Current Amplitude: " + currentAmplitude);
                        if (currentAmplitude > 1000) {
                            if (timePassed == 0) {
                                timePassed = tempTimePassed[0];
                            }
                        }
                        if (currentAmplitude > 1800 && amplitude != -1) {
                            amplitude = currentAmplitude;
                        } else {
                            if (amplitude > 1800 && currentAmplitude < 800) {
                                amplitude = -1;
                                Handler h = new Handler();
                                h.postDelayed(() -> {
                                    if ((nWord) <= 9) {
                                        mNumbers.get(nWord).setEnabled(true);
                                        nWord++;
                                    }
                                    playback();
                                }, 400);
                            }
                        }
                        tempTimePassed[0] += 50;
                    }
                });
    }

    public int getCurrentAmplitude() {
        if (mRecorder != null) {
            return mRecorder.getMaxAmplitude();
        } else return -1;
    }

    private void playback() {
        stopRecording();
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(audioFileList.get(nWord - 1).getAbsolutePath());
            mPlayer.prepare();
            mPlayer.seekTo(timePassed);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();

        mPlayer.setOnCompletionListener(mediaPlayer -> {
            if (nWord < 10) {
                timePassed = 0;
                setMediaRecorder();
                stopTimerHandler();
                startTimerHandler();
            } else {
                mViewFlipper.showNext();
                stopTimerHandler();
            }
        });
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        amplitude = 0;
    }

    @OnClick(R.id.btnFinish) void finishRecording() {
        if (mTimerHandler != null) {
            stopTimerHandler();
        }
        ArrayList<TypedFile> audioTypedFileList = new ArrayList<>();
        for (File tempFile : audioFileList) {
            audioTypedFileList.add(new TypedFile("multipart/mixed", tempFile));
        }
        System.out.println("audioTypedFileList = " + audioTypedFileList.size());

        String firstName = userRegisterInfo.getFirstName();
        String lastName = userRegisterInfo.getLastName();
        String password = userRegisterInfo.getPassword();
        String email = userRegisterInfo.getEmail();

        TypedFile photo = new TypedFile("image/*", userRegisterInfo.photoFile);
        TypedFile audio1 = new TypedFile("audio/mp3", audioFileList.get(0));
        TypedFile audio2 = new TypedFile("audio/mp3", audioFileList.get(1));
        TypedFile audio3 = new TypedFile("audio/mp3", audioFileList.get(2));
        TypedFile audio4 = new TypedFile("audio/mp3", audioFileList.get(3));
        TypedFile audio5 = new TypedFile("audio/mp3", audioFileList.get(4));
        TypedFile audio6 = new TypedFile("audio/mp3", audioFileList.get(5));
        TypedFile audio7 = new TypedFile("audio/mp3", audioFileList.get(6));
        TypedFile audio8 = new TypedFile("audio/mp3", audioFileList.get(7));
        TypedFile audio9 = new TypedFile("audio/mp3", audioFileList.get(8));
        TypedFile audio10 = null;
        try {
            audio10 = new TypedFile("application/octet-stream", audioFileList.get(9));
        } catch (Exception e) {
            e.printStackTrace();
        }

        YanrialDialog md = new YanrialDialog.Builder(this)
                .content("Please wait...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        apiClient.signup(firstName, lastName, password, email, photo, audio1, audio2, audio3, audio4, audio5, audio6, audio7, audio8, audio9, audio10, profile.getPushToken(), "android", Utils.getDeviceId(this), Utils.getDeviceName(), Utils.getDeviceVersion(), new Callback<User>() {
            @Override public void success(User user, Response response) {
                md.dismiss();
                if (user.isSuccess()) {
                    profile.setId(user.getData().getId());
                    profile.setFirstName(user.getData().getFirstName());
                    profile.setLastName(user.getData().getLastName());
                    profile.setEmail(user.getData().getEmail());
                    profile.setAccessToken(user.getData().getAccessToken());
                    profile.setCredits(user.getData().getCredits());
                    profile.setPhotoOrig(user.getData().getPhoto().getOrig());
                    profile.setPhotoThumb(user.getData().getPhoto().getThumb());
                    profile.setPassword(password);
                    profile.setLoggedIn(true);

                    // TODO: show congratulations dialog
                    CongratulationsFragmentDialog cfd = CongratulationsFragmentDialog.newInstance();
                    cfd.show(getFragmentManager(), "Congratulations");

                } else {
                    // display error for specific field,
                    // ex. "email":"some text do display"
                    for (User.Error e : user.getErrors()) {
                        Toast.makeText(RecordAudioActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }

            @Override public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

    }

    @OnClick(R.id.btnReStart) void restart() {
        for (TextView tv : mNumbers) {
            tv.setEnabled(false);
        }
        if (mTimerHandler != null) {
            stopTimerHandler();
        }
        new Handler().postDelayed(() -> {
          /*  nSampled = 0;
            nSliceCount = 0;
            nStartWord = new int[10];
            nEndWord = new int[10]; */
            deleteAudioFilesIfExist();
            audioFileList = new ArrayList<>();
            // mRecControl.done = true;
            nWord = 0;

            startTimerHandler();
            stopRecording();
            setMediaRecorder();
            if (mPlayer != null) {
                try {
                    mPlayer.stop();
                    mPlayer.release();
                } catch (Exception e) {
                    System.out.println("Exception with stopping media player");
                }
                mPlayer = null;
            }
            //setRecordingControl();
        }, 300);
    }

    @OnClick(R.id.btnReStartLater) void restartLater() {
        mViewFlipper.showPrevious();
        restart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nWord = 0;
        stopRecording();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } catch (Exception e) {

            }
        }
        //mRecControl.done = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
       /* if (mRecControl != null) {
            isPlayback = false;
        } */
    }

    private String getFilename(int index) {
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Verity/";
        filename += String.format("RecordingFile_%d.mp4", index);

        File digitFile = new File(filename);
        audioFileList.add(digitFile);

        return digitFile.getAbsolutePath();
    }

    private void deleteAudioFilesIfExist() {
        for (int i = 0; i < 10; i++) {
            String filename = String.format("RecordingFile_%d.mp4", i);
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Verity/");
            File digitFile = new File(directory, filename);
            if (digitFile.exists()) {
                digitFile.delete();
            }
        }
    }

    private void startTimerHandler() {
        mTimerHandler = new Handler();
        mTimerRunnable = new Runnable() {
            @Override public void run() {
                timerCount++;
                int minute = 0;
                int seconds = timerCount;
                if (timerCount > 59) {
                    minute = timerCount / 60;
                    seconds = timerCount - minute * 60;
                }
                mTvTimer.setText(String.format("%02d:%02d", minute, seconds));

                mTimerHandler.postDelayed(this, 1000);
            }
        };
        mTimerHandler.post(mTimerRunnable);
    }

    private void stopTimerHandler() {
        timerCount = 0;
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } catch (Exception e) {

            }
        }
        //isPlayback = false;
        BusProvider.getInstance().unregister(this);
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private short getMaxFromBuffer(short[] bufferArray) {
        short result = 0;
        for (short s : bufferArray) {
            if (s > result) result = s;
        }
        return result;
    }
}

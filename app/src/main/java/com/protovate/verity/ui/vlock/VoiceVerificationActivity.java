package com.protovate.verity.ui.vlock;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.protovate.verity.R;
import com.protovate.verity.data.Vlock;
import com.protovate.verity.ui.BaseActivity;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Yan on 5/31/15.
 */
public class VoiceVerificationActivity extends BaseActivity {
    @InjectView(R.id.btnRecord) Button mBtnRecord;
    @InjectView(R.id.equation) TextView mEquation;

    private MediaRecorder mRecorder;
    private boolean isRecording = false;
    private int count = 0;
    private boolean shouldProceed = false;
    private int amplitude = 0;

    private Vlock vlock;
    private String mFileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_verification);

        vlock = (Vlock) getIntent().getSerializableExtra("vlock");

        ButterKnife.inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Voice Verification");

        int a = generateNumber(), b = generateNumber();
        mEquation.setText(String.valueOf(a) + " + " + String.valueOf(b) + " = ?");

        vlock.setVoiceAnswer(String.valueOf(a + b));

        record();
    }

    @Override
    public void onPause() {
        super.onPause();

        /*if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        } */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shouldProceed = false;
    }

    void recordAudio() {
        startRecording();
        handler.postDelayed(handlerRunnable, 1000);
    }

    Handler handler = new Handler();
    Runnable handlerRunnable = new Runnable() {
        @Override public void run() {
            if (isRecording) {
                count += 1;
                int minute = 0;
                int seconds = count;
                if (count > 59) {
                    minute = count / 60;
                    seconds = count - minute * 60;
                }
                mBtnRecord.setText(String.format("%02d:%02d", minute, seconds) + " Click to finish");
                handler.postDelayed(this, 1000);
            }
        }
    };

    @OnClick(R.id.btnRecord) void record() {
        if (!isRecording) {
            isRecording = true;
            recordAudio();
            shouldProceed = false;
            mBtnRecord.setText("00:00 Click to finish");
        } else {
            count = 0;
            isRecording = false;
            stopRecording();

            if (shouldProceed) {
                vlock.setVoiceFile(new File(mFileName));
                Intent intent = new Intent(this, LocationActivity.class);
                intent.putExtra("vlock", vlock);
                startActivity(intent);
            } else {
                handler.removeCallbacks(handlerRunnable);

                Toast.makeText(this, "Please record answer and then proceed.", Toast.LENGTH_SHORT).show();
                isRecording = true;
                recordAudio();
                shouldProceed = false;
                mBtnRecord.setText("00:00 Click to finish");
            }
        }
    }

    private void startRecording() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/Verity/" + System.currentTimeMillis() + ".mp4";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            isRecording = false;
            shouldProceed = false;
            count = 0;
            Handler h = new Handler();
            h.postDelayed(() -> mBtnRecord.setText("Start recording"), 500);
            Toast.makeText(this, "You can't record audio at this time, make sure the other apps aren't using your microphone and try again.", Toast.LENGTH_LONG).show();
        }

        Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .map(tick -> getCurrentAmplitude())
                .takeWhile(currentAmplitude -> currentAmplitude != -1)
                .subscribe(currentAmplitude -> {
                    if (amplitude < currentAmplitude && currentAmplitude != -1) {
                        amplitude = currentAmplitude;
                        System.out.println(String.valueOf(amplitude));
                    }
                });
    }

    public int getCurrentAmplitude() {
        if (mRecorder != null) {
            return mRecorder.getMaxAmplitude();
        } else return -1;
    }

    private void stopRecording() {
        Log.d("Amplitude", String.valueOf(amplitude));
        if (amplitude < 1000) {
            shouldProceed = false;
        } else shouldProceed = true;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        amplitude = 0;
    }

    private int generateNumber() {
        Random random = new Random();
        return random.nextInt((5 - 1) + 1) + 1;
    }

}

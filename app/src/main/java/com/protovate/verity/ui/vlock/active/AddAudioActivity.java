package com.protovate.verity.ui.vlock.active;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.protovate.verity.R;
import com.protovate.verity.data.Vlock;
import com.protovate.verity.ui.BaseActivity;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Yan on 7/2/15.
 */
public class AddAudioActivity extends BaseActivity {
    @InjectView(R.id.toolbar_title) TextView mToolbarTitle;
    @InjectView(R.id.btnRecord) Button mBtnRecord;
    @InjectView(R.id.container) RelativeLayout mContainer;

    private MediaRecorder mRecorder;
    private boolean isRecording = false;
    private int count = 0;
    private boolean shouldProceed = false;
    private String mFileName;
    private Vlock vlock = new Vlock();
    private int duration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        ButterKnife.inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        setBackButton(getSupportActionBar());
        setTitle(getSupportActionBar(), "Record Audio");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    @OnClick(R.id.btnRecord) void record() {
        Handler handler = new Handler();
        if (!isRecording) {
            isRecording = true;
            startRecording();
            handler.postDelayed(new Runnable() {
                @Override public void run() {
                    if (isRecording) {
                        count += 1;
                        duration = count;
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
            }, 1000);
            shouldProceed = false;
            mBtnRecord.setText("00:00 Click to finish");
        } else {
            shouldProceed = true;
            count = 0;
            isRecording = false;
            stopRecording();

            if (shouldProceed) {
                vlock.setVoiceFile(new File(mFileName));
                Intent intent = new Intent(this, ListenAudioActivity.class);
                intent.putExtra("vlock", vlock);
                intent.putExtra("duration", duration);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please record audio.", Toast.LENGTH_SHORT).show();
            }
            mBtnRecord.setText("Start recording");
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
            Toast.makeText(this, "You can't record audio at this time, make sure the other apps aren't using your microphone and try again.", Toast.LENGTH_LONG).show();
            isRecording = false;
            count = 0;
            Handler h = new Handler();
            h.postDelayed(() -> mBtnRecord.setText("START RECORDING"), 500);
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

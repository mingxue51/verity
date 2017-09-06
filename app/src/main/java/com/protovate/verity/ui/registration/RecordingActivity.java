package com.protovate.verity.ui.registration;

/**
 * Created by Yan on 5/21/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.protovate.verity.R;
import com.protovate.verity.data.Profile;
import com.protovate.verity.data.RegisterInfo;
import com.protovate.verity.data.responses.User;
import com.protovate.verity.service.ApiClient;
import com.protovate.verity.ui.adapters.NumberGridAdapter;
import com.protovate.verity.utils.RecordingControl;
import com.protovate.verity.utils.RecordingControl.OnAudioReadingListener;
import com.protovate.verity.utils.Utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class RecordingActivity extends Activity implements OnAudioReadingListener, Callback<User> {
    @Inject ApiClient apiClient;
    @Inject Profile profile;
    @InjectView(R.id.btnStart) Button btnStart;
    @InjectView(R.id.timerDisplay) TextView lblTimer;
    @InjectView(R.id.digitGridView) GridView digitGridView;

    private RecordingControl mRecControl;
    private int nSliceCount = 0;
    private boolean isStartWord = false;
    private static int nWord = 0;
    private int maxBufferSize = 8000 * 20;
    private int nSampled = 0;
    private short[] audioBuffer = new short[maxBufferSize];
    private int[] nStartWord = new int[10];
    private int[] nEndWord = new int[10];
    public ArrayList<File> audioFileList = new ArrayList<>();
    private boolean isStartStop = false;

    private java.util.Timer timer = null;
    private int timerCount = 0;
    private RegisterInfo userRegisterInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        ButterKnife.inject(this);
        mRecControl = new RecordingControl();
        mRecControl.setOnEventListener(this);

        lblTimer.setText(displayTimerString());
        digitGridView.setAdapter(new NumberGridAdapter(this));
        userRegisterInfo = (RegisterInfo) getIntent().getSerializableExtra("UserRegisterInfo");
    }

    @Override
    public void onReadingEvent(short[] buffer) {

        for (short tempSample : buffer) {
            int nThreshold = 4000;
            nSampled++;

            if (nSampled >= maxBufferSize)
                nSampled = 0;

            if (!isStartWord && (Math.abs(tempSample) > nThreshold)) {
                isStartWord = true;
                nStartWord[nWord] = nSampled;
                runOnUiThread(() -> {
                    if (nWord <= 9) onChangeDisplay(nWord);
                });
            }

            if (Math.abs(tempSample) > nThreshold) {
                nSliceCount = 0;

            } else {
                nSliceCount++;
                int nSliceLength = 4000;

                if (isStartWord && (nSliceCount > nSliceLength)) {
                    isStartWord = false;
                    nEndWord[nWord] = nSampled - nSliceLength;
                    nWord++;
                    if (nWord == 10) {
                        isStartStop = false;
                        runOnUiThread(() -> {
                            mRecControl.stop();
                            timer.cancel();
                            btnStart.setText("Start");
                        });
                    }
                    Thread thread = new Thread(() -> {
                        onWriteAudioDataToFile(nWord - 1);
                    });
                    thread.start();
                    nSliceCount = 0;
                    return;
                }
            }
        }
    }

    @Override public void onAmplitudeChange(double amplitude) {
        System.out.println("amplitude = " + amplitude);
    }

    @Override public void update(short[] bytes, int length, float sampleLength) {

    }

    private String displayTimerString() {
        int n1 = timerCount / 60;
        int n2 = timerCount % 60;
        String temp = String.format("%02d : %02d", n1, n2);
        return temp;
    }

    private void onWriteAudioDataToFile(int index) {
        String filename = getFilename(index);
        DataOutputStream oos;
        try {
            oos = new DataOutputStream(new FileOutputStream(filename));
            for (int i = nStartWord[index]; i < nEndWord[index]; i++) {
                try {
                    oos.writeShort(audioBuffer[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
    }

    private String getFilename(int index) {
        String filename = String.format("RecordingFile_%d", index);
        String filepath = "FileStorage";
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        File digitFile = new File(directory, filename);
        audioFileList.add(digitFile);
        return digitFile.getAbsolutePath();
    }

    private void onChangeDisplay(int index) {
        TextView tempView = (TextView) digitGridView.getChildAt(index);
        tempView.setTextColor(Color.BLUE);
    }

    @OnClick(R.id.btnFinish) void onRegistering() {
        ArrayList<TypedFile> audioTypedFileList = new ArrayList<>();
        for (File tempFile : audioFileList) {
            audioTypedFileList.add(new TypedFile("multipart/mixed", tempFile));
        }
        String firstName = userRegisterInfo.getFirstName();
        String lastName = userRegisterInfo.getLastName();
        String password = userRegisterInfo.getPassword();
        String email = userRegisterInfo.getEmail();

        TypedFile photo = new TypedFile("multipart/mixed", userRegisterInfo.photoFile);
        TypedFile audio1 = new TypedFile("multipart/mixed", audioFileList.get(0));
        TypedFile audio2 = new TypedFile("multipart/mixed", audioFileList.get(1));
        TypedFile audio3 = new TypedFile("multipart/mixed", audioFileList.get(2));
        TypedFile audio4 = new TypedFile("multipart/mixed", audioFileList.get(3));
        TypedFile audio5 = new TypedFile("multipart/mixed", audioFileList.get(4));
        TypedFile audio6 = new TypedFile("multipart/mixed", audioFileList.get(5));
        TypedFile audio7 = new TypedFile("multipart/mixed", audioFileList.get(6));
        TypedFile audio8 = new TypedFile("multipart/mixed", audioFileList.get(7));
        TypedFile audio9 = new TypedFile("multipart/mixed", audioFileList.get(8));
        TypedFile audio10 = new TypedFile("multipart/mixed", audioFileList.get(9));
        apiClient.signup(firstName, lastName, password, email, photo, audio1, audio2, audio3, audio4, audio5, audio6, audio7, audio8, audio9, audio10, profile.getPushToken(), "android", Utils.getDeviceId(this), Utils.getDeviceName(), Utils.getDeviceVersion(), this);
    }

    @OnClick(R.id.btnRecordingCancel) void onStopRecord() {
        mRecControl.stop();
    }

    @OnClick(R.id.btnStart) void onStartRecord() {

        if (isStartStop) {
            isStartStop = false;
            mRecControl.stop();
            timer.cancel();
            btnStart.setText("Start");
        } else {
            isStartStop = true;

            if (timer != null) {
                timer.cancel();
            }
            timer = new java.util.Timer();
            timer.scheduleAtFixedRate(new TimerTask(), 0, 1000);
            mRecControl.start();
            btnStart.setText("Stop");
        }
    }

    class TimerTask extends java.util.TimerTask {
        @Override
        public void run() {
            timerCount++;
            runOnUiThread(() -> lblTimer.setText(displayTimerString()));
        }
    }

    @Override
    public void success(User user, Response response) {
        if (user.isSuccess()) {
            Toast.makeText(this, "Registering OK.", Toast.LENGTH_SHORT).show();
        } else {
            // display error for specific field,
            // ex. "email":"some text do display"
            Toast.makeText(this, "Registering Failure.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        // means there is no internet because we dont have status codes in backend
    }

}
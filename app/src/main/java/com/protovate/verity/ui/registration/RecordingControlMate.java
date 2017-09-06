package com.protovate.verity.ui.registration;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by Yan on 6.8.2015..
 */
public class RecordingControlYan implements Runnable {
    int mBufferSize;
    short[] buffer;
    byte[] byteBuffer;

    public boolean done = false;
    AudioRecord mAudio;
    OnUpdateListener mListener;

    public static final int REQUIRED_FREQUENCY = 44100;
    public static final int REQUIRED_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    public static final int REQUIRED_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final float PCM_MAXIMUM_VALUE = 32768.0f;            // 16-bit signed = 32768

    public interface OnUpdateListener {
        public void update(short[] bytes, int length, float sampleLength);

        public void onReadingEvent(short[] buffer);
    }

    public RecordingControlYan(OnUpdateListener listener) {
        this.mListener = listener;
        mBufferSize = AudioRecord.getMinBufferSize(REQUIRED_FREQUENCY, REQUIRED_CHANNEL, REQUIRED_FORMAT);
        if (mBufferSize > 0) {
//			mBufferSize *= 2;
            buffer = new short[mBufferSize];
            //byteBuffer = new byte[mBufferSize];
            mAudio = new AudioRecord(MediaRecorder.AudioSource.MIC, REQUIRED_FREQUENCY, REQUIRED_CHANNEL, REQUIRED_FORMAT, mBufferSize);
            if (mAudio.getState() == AudioRecord.STATE_UNINITIALIZED) {
                mAudio = null;
            }
        }
    }

    public int getmBufferSize() {
        return mBufferSize;
    }

    @Override
    public void run() {
        if (mAudio != null) {
            mAudio.startRecording();

            while (!done) {
                int count = mAudio.read(buffer, 0, mBufferSize);
                //int count2 = mAudio.read(byteBuffer, 0, mBufferSize);
                if (count > 0) {
                    if (mListener != null) {
                        float sampleLength = 1.0f / ((float) REQUIRED_FREQUENCY / (float) count);
                        mListener.update(buffer, count, sampleLength);
                        mListener.onReadingEvent(buffer);
                    }
                } else {
                    done = true;
                }
            }

            mAudio.stop();
            mAudio.release();
            mAudio = null;
        } else {
            Log.d("Control", "mAudio was null!");
        }
    }
}

package com.protovate.verity.utils;

/**
 * Created by Yan on 5/22/2015.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class RecordingControl {
    private AudioRecord audioRecord = null;
    private static final int RECORDER_SAMPLE_RATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static int nBufferSize = RECORDER_SAMPLE_RATE / 2;// for 0.5s
    public static final float PCM_MAXIMUM_VALUE = 32768.0f;            // 16-bit signed = 32768

    private static short[] mInBuffer = new short[nBufferSize];
    private static boolean isRunning = false;
    private static boolean isStopped = true;
    private static int n_Words = 0;
    private OnAudioReadingListener eventListener = null;

    public RecordingControl() {
        //init
    }

    public synchronized static void setStop(boolean bStop) {
        isStopped = bStop;
    }

    public synchronized static void setRunning(boolean bRunning) {
        isRunning = bRunning;
    }

    public synchronized static boolean isStopped() {
        return isStopped;
    }

    public synchronized static boolean isRunning() {
        return isRunning;
    }

    public synchronized static int getWords() {
        return n_Words;
    }

    public synchronized static void setWords(int nWord) {
        n_Words = nWord;
    }

    public void setBufferSize() {
        this.stop();
        if (mInBuffer.length != nBufferSize) {
            int minSize = getMinBufferSize();

            if (nBufferSize < minSize) {
                nBufferSize = minSize;
            }

            mInBuffer = new short[nBufferSize];
        }
    }

    public void setOnEventListener(OnAudioReadingListener listener) {
        eventListener = listener;
    }

    public static int getMinBufferSize() {
        int nSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        return nSize;
    }

    public boolean isPlaying() {
        return isRunning;
    }

    public void start() {
        setBufferSize();

        if (audioRecord == null) {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, mInBuffer.length * 2);
        }

        try {
            audioRecord.startRecording();
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        setRunning(true);


        Thread thread = new Thread(() -> {
            setStop(false);
            while (isRunning()) {
                int read = audioRecord.read(mInBuffer, 0, mInBuffer.length);
                int amplitude = (mInBuffer[0] & 0xff) << 8 | mInBuffer[1];
                double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude) / 32768);

                if (eventListener != null) {

                    eventListener.onReadingEvent(readSample());
                    eventListener.onAmplitudeChange(amplitudeDb);
                    float sampleLength = 1.0f / ((float) RECORDER_SAMPLE_RATE / (float) read);
                    eventListener.update(mInBuffer, read, sampleLength);
                }
            }
            setStop(true);
        });
        thread.start();
    }

    public int getBufferSize() {
        return mInBuffer.length;
    }

    public void stop() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        setRunning(false);
        audioRecord = null;
        while (!isStopped()) ;
    }

    public void release() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    private short[] readSample() {
        if (audioRecord != null) {
            audioRecord.read(mInBuffer, 0, mInBuffer.length);
            return mInBuffer;
        }
        return null;
    }

    public interface OnAudioReadingListener {
        void onReadingEvent(short[] buffer);

        void onAmplitudeChange(double amplitude);

        void update(short[] bytes, int length, float sampleLength);
    }
}



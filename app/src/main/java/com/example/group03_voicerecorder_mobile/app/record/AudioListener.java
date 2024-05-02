package com.example.group03_voicerecorder_mobile.app.record;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AudioListener {
    private final Context context;
    private AudioRecord audioRecord;
    private boolean isListening;
    private static final int SAMPLE_RATE = 8000;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AMPLITUDE_THRESHOLD = 150; //threshold for auto-record trigger
    private final int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    public AudioListener(Context context) {
        this.context = context;
    }

    public void startListening() {
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
            audioRecord.startRecording();
            isListening = true;
            new Thread(this::processAudioStream).start();
        } else {
            throw new IllegalStateException("Permission not granted to record audio");
        }
    }

    private void processAudioStream() {
        short[] buffer = new short[bufferSize];
        while (isListening) {
            int read = audioRecord.read(buffer, 0, bufferSize);
            if (read > 0) {
                int amp = calculateAmplitude(buffer, read);
                if (amp > AMPLITUDE_THRESHOLD) {
                    startRecordingService();
                    break;
                }
            }
        }
    }

    private void startRecordingService() {
        Intent intent = new Intent(context, RecordService.class);
        context.startService(intent.setAction("ACTION_START_RECORDING"));
    }

    private int calculateAmplitude(short[] buffer, int read) {
        int maxAmplitude = 0;
        for (int i = 0; i < read; i++) {
            maxAmplitude = Math.max(maxAmplitude, Math.abs(buffer[i]));
        }
        return maxAmplitude;
    }

    public void stopListening() {
        if (audioRecord != null) {
            isListening = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

}

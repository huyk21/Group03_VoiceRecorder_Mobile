package com.example.group03_voicerecorder_mobile.app.record;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.main.WaveformView;

public class RecordActivity extends AppCompatActivity {
    private TextView appName, status;
    private ImageButton toRecords, toMenu, playBtn, record_stopBtn, pauseBtn;
    private Chronometer chronometer;
    private WaveformView waveformView;
    private boolean isRecording = false;
    private long timeWhenPaused = 0;

    // Audio recording settings
    private static final int SAMPLE_RATE = 44100; // Sample rate in Hz
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audioRecorder;
    private Thread recordingThread;
    private int bufferSize;

    // Handler for posting updates to the UI thread
    private Handler uiHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // Initialize views
        appName = findViewById(R.id.appName);
        status = findViewById(R.id.recordStatus);
        chronometer = findViewById(R.id.chronometer);
        waveformView = findViewById(R.id.waveformView);
        playBtn = findViewById(R.id.playBtn);
        record_stopBtn = findViewById(R.id.btnRecord_Stop);
        pauseBtn = findViewById(R.id.btn_pause);

        // Setup button click listeners
        setupButtonClickListeners();

        // Prepare the AudioRecord
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecorder = new AudioRecord(AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
    }

    private void setupButtonClickListeners() {
        record_stopBtn.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        pauseBtn.setOnClickListener(v -> {
            if (isRecording) {
                pauseRecording();
            }
        });

        playBtn.setOnClickListener(v -> {
            if (!isRecording) {
                resumeRecording();
            }
        });
    }

    private void startRecording() {
        if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Toast.makeText(this, "Audio Record can't initialize!", Toast.LENGTH_SHORT).show();
            return;
        }

        audioRecorder.startRecording();
        isRecording = true;
        status.setText("Recording...");
        chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
        chronometer.start();

        // Start recording thread
        recordingThread = new Thread(this::readAudioData, "AudioRecorder Thread");
        recordingThread.start();

        // UI updates for recording state
        updateRecordingButtons();
    }

    private void readAudioData() {
        short[] audioBuffer = new short[bufferSize];
        while (isRecording) {
            int numberOfShort = audioRecorder.read(audioBuffer, 0, audioBuffer.length);
            float maxAmplitude = 0;
            for (int i = 0; i < numberOfShort; i++) {
                maxAmplitude += Math.abs(audioBuffer[i]);
            }
            maxAmplitude /= numberOfShort;
            final float finalMaxAmplitude = maxAmplitude;
            uiHandler.post(() -> waveformView.addAmplitude(finalMaxAmplitude));
        }
    }

    private void stopRecording() {
        // Stop recording and release resources
        isRecording = false;
        audioRecorder.stop();
        audioRecorder.release();
        recordingThread = null;
        status.setText("Recording stopped.");
        chronometer.stop();
        timeWhenPaused = SystemClock.elapsedRealtime() - chronometer.getBase();

        // UI updates for non-recording state
        updateRecordingButtons();
    }

    private void pauseRecording() {
        // Pause recording logic here...
        isRecording = false;
        audioRecorder.stop();
        status.setText("Recording Paused.");
        chronometer.stop();
        timeWhenPaused = SystemClock.elapsedRealtime() - chronometer.getBase();

        // UI updates for paused state
        updateRecordingButtons();
    }

    private void resumeRecording() {
        // Resume recording logic here...
        audioRecorder.startRecording();
        isRecording = true;
        status.setText("Recording Resumed.");
        chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
        chronometer.start();

        // Restart recording thread
        recordingThread = new Thread(this::readAudioData, "AudioRecorder Thread");
        recordingThread.start();

        // UI updates for recording state
        updateRecordingButtons();
    }

    private void updateRecordingButtons() {
        // UI updates for button states...
        playBtn.setVisibility(isRecording ? View.INVISIBLE : View.VISIBLE);
        pauseBtn.setVisibility(isRecording ? View.VISIBLE : View.INVISIBLE);
        record_stopBtn.setImageResource(isRecording ? R.drawable.ic_stop : R.drawable.ic_record);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecorder != null) {
            audioRecorder.release();
        }

        if (recordingThread != null) {
            isRecording = false;
            recordingThread.interrupt();
        }
    }
}

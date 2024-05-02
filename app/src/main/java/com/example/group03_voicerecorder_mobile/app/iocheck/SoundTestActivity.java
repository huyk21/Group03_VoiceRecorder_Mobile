package com.example.group03_voicerecorder_mobile.app.iocheck;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import java.io.IOException;

public class SoundTestActivity extends AppCompatActivity {

    private final int recordingTime = 5; //5 seconds
    private ImageButton backBtn;
    private TextView indicator;
    private Chronometer timer;
    private ImageButton recordBtn;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String filePath = null;
    private Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Utilities.setCustomTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundtest);

        backBtn = findViewById(R.id.btnBack);
        indicator = findViewById(R.id.indicator);
        timer = findViewById(R.id.timer);
        recordBtn = findViewById(R.id.btnRecord);

        backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        recordBtn.setOnClickListener(v -> {
            if (checkPermissions()) {
                indicator.setText("Say something into the microphone");
                timer.setVisibility(View.VISIBLE);
                configureChronometer(recordingTime);
                startRecording();
            }
        });

        filePath = getExternalCacheDir().getAbsolutePath() + "/audiorecordtest.3gp";
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            return false;
        }
        return true;
    }

    private void startRecording() {
        setupMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            recordBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecording();
                    configureChronometer(recordingTime);
                    indicator.setText("Playing back the recorded audio");
                    startPlayback();
                }
            }, recordingTime * 1000); // Adjust time as needed
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(filePath);
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void startPlayback() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void configureChronometer(int endTimerSec) {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        handler.postDelayed(() -> {
            timer.stop();
            timer.setBase(SystemClock.elapsedRealtime());
        }, endTimerSec * 1000L); // Convert seconds to milliseconds
    }
}

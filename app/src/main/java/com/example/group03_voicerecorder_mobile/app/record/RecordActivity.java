package com.example.group03_voicerecorder_mobile.app.record;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.main.WaveformView;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    private TextView appName, status;
    private DatabaseHelper databaseHelper;
    private ImageButton toRecords, toMenu, playBtn, record_stopBtn, pauseBtn, backBtn;
    private Chronometer chronometer;
    private boolean isRecording = false;
    private boolean isPausing = false;
    private long timeWhenPaused = 0;
    private MediaRecorder mediaRecorder;
    private String currentFilePath;
    private ArrayList<Integer> amplitudeList = new ArrayList<>();
    private Handler waveformHandler = new Handler();
    private WaveformView waveformView;
    private BroadcastReceiver broadcastReceiver;
    public static final String RECORDING_STATUS_UPDATE = "recording_status_update";


    private Runnable updateWaveformRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording && mediaRecorder != null) {
                int maxAmplitude = mediaRecorder.getMaxAmplitude();
                amplitudeList.add(maxAmplitude); // Store amplitude in the list
                waveformView.addAmplitude(maxAmplitude); // Scale this value if necessary
                waveformHandler.postDelayed(this, 100); // Update the waveform every 100 milliseconds
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        databaseHelper = new DatabaseHelper(this);
        waveformView = findViewById(R.id.waveformView);
        appName = findViewById(R.id.toRecords);
        status = findViewById(R.id.recordStatus);
        chronometer = findViewById(R.id.chronometer);
        playBtn = findViewById(R.id.playBtn);
        record_stopBtn = findViewById(R.id.btnRecord_Stop);
        pauseBtn = findViewById(R.id.btn_pause);
        backBtn = findViewById(R.id.btnBack);
        currentFilePath = getExternalFilesDir(null).getAbsolutePath() + "/" + GlobalConstants.DEFAULT_RECORD_NAME + " " + System.currentTimeMillis() / 1000 + GlobalConstants.FORMAT_M4A;

        backBtn.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        // Setup button click listeners
        setupButtonClickListeners();
        broadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(RECORDING_STATUS_UPDATE));
    }

    private void setupButtonClickListeners() {
        record_stopBtn.setOnClickListener(v -> {
            if (!isRecording && !isPausing) {
                //startRecording();
                startRecordService();
            } else {
                //stopRecording();
                stopRecordService();
            }
        });

        pauseBtn.setOnClickListener(v -> {
            if(isRecording){
                //pauseRecording();
                pauseRecordService();
            }
            else{
                //resumeRecording();
                resumeRecordService();
            }
        });
    }

    private void startRecordService() {
        startService(new Intent(this, RecordService.class).setAction("ACTION_START_RECORDING"));
        record_stopBtn.setImageResource(R.drawable.ic_stop);
        status.setText("Recording...");
        isRecording = true;
        chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
        chronometer.start();
        waveformHandler.post(updateWaveformRunnable);
        updateRecordingButtons();
    }

    private void stopRecordService() {
        startService(new Intent(this, RecordService.class).setAction("ACTION_STOP_RECORDING"));
        record_stopBtn.setImageResource(R.drawable.ic_record);
        status.setText("Recording stopped.");
        isRecording = false;
        chronometer.stop();
        updateRecordingButtons();
        waveformHandler.removeCallbacks(updateWaveformRunnable);
    }

    private void pauseRecordService() {
        startService(new Intent(this, RecordService.class).setAction("ACTION_PAUSE_RECORDING"));
        pauseBtn.setImageResource(R.drawable.baseline_play_circle_24);
        status.setText("Recording paused.");
        isPausing = true;
        isRecording = false;
        status.setText("Recording Paused");
        chronometer.stop();
        // Calculate the time elapsed before pausing to adjust the chronometer base when resuming
        waveformHandler.removeCallbacks(updateWaveformRunnable);
        updateRecordingButtons(); // Update the UI when recording is paused.
    }

    private void resumeRecordService() {
        startService(new Intent(this, RecordService.class).setAction("ACTION_RESUME_RECORDING"));
        pauseBtn.setImageResource(R.drawable.ic_pause);
        status.setText("Recording...");
        isPausing = false;
        isRecording = true;
        chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
        chronometer.start();
        waveformHandler.post(updateWaveformRunnable);
        updateRecordingButtons();
    }

    private void pauseRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.pause();
            isRecording = false;
            isPausing = true;
            status.setText("Recording Paused");
            chronometer.stop();
            // Calculate the time elapsed before pausing to adjust the chronometer base when resuming
            timeWhenPaused = SystemClock.elapsedRealtime() - chronometer.getBase();
            waveformHandler.removeCallbacks(updateWaveformRunnable);
            updateRecordingButtons(); // Update the UI when recording is paused.
        }
    }

    private void resumeRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.resume();
            isRecording = true;
            status.setText("Recording...");
            // Set the chronometer base to the current time minus the amount of time that had already elapsed before pausing
            // This effectively continues the chronometer from where it left off
            chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
            chronometer.start();
            waveformHandler.post(updateWaveformRunnable);
            updateRecordingButtons();
        }
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioChannels(1);
        record_stopBtn.setImageResource(R.drawable.ic_stop);
        currentFilePath = getExternalFilesDir(null).getAbsolutePath() + "/" + GlobalConstants.DEFAULT_RECORD_NAME + " " + System.currentTimeMillis() / 1000 + GlobalConstants.FORMAT_M4A;
        mediaRecorder.setOutputFile(currentFilePath);

        try {
            mediaRecorder.prepare();

            mediaRecorder.start();
            isRecording = true;
            status.setText("Recording...");
            chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
            chronometer.start();
            waveformHandler.post(updateWaveformRunnable);
            updateRecordingButtons();
        } catch (IOException e) {
            Toast.makeText(this, "Recording failed to start", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateRecordingButtons() {
        // Here, we'll manage the visibility and appearance of buttons based on the recording state.
        if (isRecording) {
            // Recording is ongoing
            pauseBtn.setImageResource(R.drawable.ic_pause);

        } else {
            // Recording is paused or stopped
            pauseBtn.setImageResource(R.drawable.baseline_play_circle_24);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int maxAmplitude = intent.getIntExtra("recording_status", 0);
            // update ui
            waveformView.addAmplitude(maxAmplitude);
        }
    }
}
package com.example.group03_voicerecorder_mobile.app.record;
import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private TextView status;
    private DatabaseHelper databaseHelper;
    private ImageButton playBtn, record_stopBtn, pauseBtn, backBtn;
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
    private static final int PERMISSION_REQUEST_CODE = 101;


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

    public void checkPermission() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        // Check if each permission is granted, if not, request them
        boolean permissionsNeeded = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded = true;
                break;
            }
        }

        if (permissionsNeeded) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean allPermissionsGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted) {
                    Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Not all permissions were granted", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permissions request denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        checkPermission();
        databaseHelper = new DatabaseHelper(this);
        waveformView = findViewById(R.id.waveformView);
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
                startRecordService();
            } else {
                stopRecordService();
            }
        });

        pauseBtn.setOnClickListener(v -> {
            if(isRecording){
                pauseRecordService();
            }
            else{
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
        finish();
    }

    private void pauseRecordService() {
        startService(new Intent(this, RecordService.class).setAction("ACTION_PAUSE_RECORDING"));
        pauseBtn.setImageResource(R.drawable.ic_play);
        status.setText("Recording paused.");
        isPausing = true;
        isRecording = false;
        status.setText("Recording Paused");
        chronometer.stop();
        timeWhenPaused = SystemClock.elapsedRealtime() - chronometer.getBase();
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

    private void updateRecordingButtons() {
        // Here, we'll manage the visibility and appearance of buttons based on the recording state.
        if (isRecording) {
            // Recording is ongoing
            pauseBtn.setImageResource(R.drawable.ic_pause);

        } else {
            // Recording is paused or stopped
            pauseBtn.setImageResource(R.drawable.ic_play);
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
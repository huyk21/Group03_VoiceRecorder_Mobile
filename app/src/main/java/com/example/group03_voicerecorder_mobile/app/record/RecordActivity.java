package com.example.group03_voicerecorder_mobile.app.record;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.main.WaveformView;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import java.util.ArrayList;

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
                Manifest.permission.READ_PHONE_STATE
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            record_stopBtn.setImageResource(R.drawable.ic_stop);
            status.setText("Recording...");
            long elapsedTime = PreferenceHelper.getElapsedTime(this, "ElapsedTime");
            chronometer.setBase(elapsedTime);
            chronometer.start();
            waveformHandler.post(updateWaveformRunnable);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utilities.setCustomTheme(this);

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
        registerReceiver(broadcastReceiver, new IntentFilter(RECORDING_STATUS_UPDATE), Context.RECEIVER_NOT_EXPORTED);
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
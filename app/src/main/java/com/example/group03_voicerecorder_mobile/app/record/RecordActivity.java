package com.example.group03_voicerecorder_mobile.app.record;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.main.WaveformView;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    private TextView appName, status;
    private DatabaseHelper databaseHelper;
    private ImageButton toRecords, toMenu, playBtn, record_stopBtn, pauseBtn;
    private Chronometer chronometer;
    private boolean isRecording = false;
    private long timeWhenPaused = 0;
    private MediaRecorder mediaRecorder;
    private String currentFilePath;
    private Handler waveformHandler = new Handler();
    private WaveformView waveformView;
    private Runnable updateWaveformRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording && mediaRecorder != null) {
                int maxAmplitude = mediaRecorder.getMaxAmplitude();
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
        appName = findViewById(R.id.appName);
        status = findViewById(R.id.recordStatus);
        chronometer = findViewById(R.id.chronometer);
        playBtn = findViewById(R.id.playBtn);
        record_stopBtn = findViewById(R.id.btnRecord_Stop);
        pauseBtn = findViewById(R.id.btn_pause);

        // Setup button click listeners
        setupButtonClickListeners();
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
            if(isRecording){
                pauseRecording();
            }
            else{
                resumeRecording();
            }

        });


    }
    private void pauseRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.pause();
            isRecording = false;
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

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            status.setText("Recording stopped.");
            chronometer.stop();
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            timeWhenPaused = 0;

            updateRecordingButtons();

            String fileName = currentFilePath.substring(currentFilePath.lastIndexOf('/') + 1,
                    currentFilePath.lastIndexOf('.'));
            Record record = new Record();
            record.setFilePath(currentFilePath);
            record.setFilename(fileName);
            record.setDurationMillis(elapsedMillis);
            record.setFilename(fileName);

            long newRowId = databaseHelper.addRecording(record);
            waveformHandler.removeCallbacks(updateWaveformRunnable);

            if (newRowId != -1) {
                Toast.makeText(this, "Recording saved to database.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save recording.", Toast.LENGTH_SHORT).show();
            }
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
        if (mediaRecorder != null) {
            Log.e("RecordActivity", "MediaRecorder is not null");
            mediaRecorder.release();
        }
    }
}

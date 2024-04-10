package com.example.group03_voicerecorder_mobile.app.record;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;

import java.io.IOException;
import java.util.Date;

public class RecordActivity extends AppCompatActivity {
    private TextView appName, status;
    private DatabaseHelper databaseHelper;
    private ImageButton toRecords, toMenu, playBtn, record_stopBtn, pauseBtn;
    private Chronometer chronometer;
    private boolean isRecording = false;
    private long timeWhenPaused = 0;
    private MediaRecorder mediaRecorder;
    private String currentFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        databaseHelper = new DatabaseHelper(this);

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
            pauseRecording();
        });

        playBtn.setOnClickListener(v -> {
            resumeRecording();
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
            updateRecordingButtons(); // Update the UI when recording is resumed.
        }
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioChannels(1);

        currentFilePath = getExternalFilesDir(null).getAbsolutePath() + "/recording.m4a";
        mediaRecorder.setOutputFile(currentFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            status.setText("Recording...");
            chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
            chronometer.start();
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

            Date timestamp = new Date(); // Current time as the end of the recording
            Record record = new Record(currentFilePath, elapsedMillis, timestamp, 0);

            long newRowId = databaseHelper.addRecording(record);

            if (newRowId != -1) {
                Toast.makeText(this, "Recording saved to database.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save recording.", Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    private void updateRecordingButtons() {
        // Here, we'll manage the visibility and appearance of buttons based on the recording state.
        if (isRecording) {
            // Recording is ongoing

            pauseBtn.setVisibility(View.VISIBLE); // Show pause button
            playBtn.setVisibility(View.GONE); // Hide resume button
        } else {
            // Recording is paused or stopped

            pauseBtn.setVisibility(View.GONE); // Hide pause button
            playBtn.setVisibility(isRecording ? View.GONE : View.VISIBLE); // Show resume button if recording is paused
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

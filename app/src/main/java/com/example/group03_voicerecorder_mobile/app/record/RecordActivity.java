package com.example.group03_voicerecorder_mobile.app.record;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.audio.recorder.RecordService;

public class RecordActivity extends AppCompatActivity {
    private TextView appName, status;
    private ImageButton toRecords, toMenu, playBtn, record_stopBtn, pauseBtn;
    private Chronometer chronometer;
    private boolean mStartRecording = true, mPauseRecording = false;
    private long timeWhenPaused = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        appName = findViewById(R.id.appName);

        toMenu = findViewById(R.id.toMenu);
        status = findViewById(R.id.recordStatus);
        chronometer = findViewById(R.id.chronometer);
        playBtn = findViewById(R.id.playBtn);
        record_stopBtn = findViewById(R.id.btnRecord_Stop);
        pauseBtn = findViewById(R.id.btn_pause);

        record_stopBtn.setOnClickListener(v -> {
            if (mStartRecording) {
                startRecording();
            } else {
                stopRecording();
            }
            mStartRecording = !mStartRecording; // Toggle the recording state
        });

        pauseBtn.setOnClickListener(v -> {
            pauseRecording();
            mPauseRecording = true; // Set pause state to true
            playBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.INVISIBLE);
        });

        playBtn.setOnClickListener(v -> {
            resumeRecording();
            mPauseRecording = false; // Set pause state to false
            playBtn.setVisibility(View.INVISIBLE);
            pauseBtn.setVisibility(View.VISIBLE);
        });
    }

    private void startRecording() {
        Intent intent = new Intent(this, RecordService.class);
        intent.setAction("ACTION_START_RECORDING");
        startService(intent);
        Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();

        record_stopBtn.setImageResource(R.drawable.ic_stop);
        pauseBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.INVISIBLE); // Initially, the play button is hidden
        status.setText(R.string.status_record);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void stopRecording() {
        Intent intent = new Intent(this, RecordService.class);
        intent.setAction("ACTION_STOP_RECORDING");
        startService(intent); // You may choose to use stopService if your service design requires it
        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();

        record_stopBtn.setImageResource(R.drawable.ic_record);
        pauseBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
        status.setText(R.string.status_not_record);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        timeWhenPaused = 0;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void pauseRecording() {
        Intent intent = new Intent(this, RecordService.class);
        intent.setAction("ACTION_PAUSE_RECORDING");
        startService(intent);
        Toast.makeText(this, "Recording Paused", Toast.LENGTH_SHORT).show();

        status.setText(R.string.paused_status);
        chronometer.stop();
        timeWhenPaused = SystemClock.elapsedRealtime() - chronometer.getBase();

    }

    private void resumeRecording() {
        Intent intent = new Intent(this, RecordService.class);
        intent.setAction("ACTION_RESUME_RECORDING");
        startService(intent);
        Toast.makeText(this, "Recording Resumed", Toast.LENGTH_SHORT).show();

        status.setText(R.string.status_record);
        chronometer.setBase(SystemClock.elapsedRealtime() - timeWhenPaused);
        chronometer.start();
    }
}

package com.example.group03_voicerecorder_mobile.app.record;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.audio.recorder.AudioRecorder;
import com.example.group03_voicerecorder_mobile.audio.recorder.RecorderInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class RecordActivity extends AppCompatActivity {
    private TextView appName;
    private Button toRecords;
    private ImageButton toMenu;
    private TextView status;
    private Chronometer chronometer;
    private ImageButton playBtn;
    private ImageButton record_pauseBtn;
    private ImageButton stopBtn;
    private AudioRecorder recorder = new AudioRecorder();

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;
    long timeWhenPaused = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        appName = findViewById(R.id.appName);
        toRecords = findViewById(R.id.toRecords);
        toMenu = findViewById(R.id.toMenu);
        status = findViewById(R.id.recordStatus);
        chronometer = findViewById(R.id.chronometer);
        playBtn = findViewById(R.id.playBtn);
        record_pauseBtn = findViewById(R.id.btnRecord_Pause);
        stopBtn = findViewById(R.id.btnStop);

        record_pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });
    }

    public void startRecord(boolean start) {
        //Intent intent = new Intent(getApplicationContext(), RecordingService.class);

        if (start) {
            playBtn.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.VISIBLE);
            record_pauseBtn.setImageResource(R.drawable.ic_pause);

            Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();

            File folder = new File(Environment.getExternalStorageDirectory() + GlobalConstants.STORAGE_DIR);
            if (!folder.exists()) {
                folder.mkdir();
            }

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            status.setText(R.string.status_record);
            //startService(intent);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        else {
            record_pauseBtn.setImageResource(R.drawable.ic_record);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            status.setText(R.string.status_not_record);

            //stopService(intent);
        }
    }
}

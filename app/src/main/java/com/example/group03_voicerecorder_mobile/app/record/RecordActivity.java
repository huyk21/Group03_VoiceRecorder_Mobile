package com.example.group03_voicerecorder_mobile.app.record;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecordActivity extends AppCompatActivity {
    private TextView appName;
    private Button toRecords;
    private ImageButton toMenu;
    private TextView timer;
    private FloatingActionButton playBtn;
    private FloatingActionButton record_pauseBtn;
    private FloatingActionButton stopBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        appName = findViewById(R.id.appName);
        toRecords = findViewById(R.id.toRecords);
        toMenu = findViewById(R.id.toMenu);
        timer = findViewById(R.id.timerTextView);
        playBtn = findViewById(R.id.playButton);
        record_pauseBtn = findViewById(R.id.record_pauseButton);
        stopBtn = findViewById(R.id.stopButton);


    }
}

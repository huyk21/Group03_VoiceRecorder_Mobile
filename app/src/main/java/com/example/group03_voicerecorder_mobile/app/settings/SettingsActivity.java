package com.example.group03_voicerecorder_mobile.app.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.statistics.StatisticsActivity;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnToStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btnBack);
        btnToStatistics = findViewById(R.id.btnToStatistics);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnToStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toStatisticsActivity();
            }
        });
    }
    private void toStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }
}

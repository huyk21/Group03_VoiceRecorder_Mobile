package com.example.group03_voicerecorder_mobile.app.statistics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;

public class StatisticsActivity extends AppCompatActivity {
    private TextView totalRecords;
    private TextView totalDuration;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        totalRecords = findViewById(R.id.totalRecords);
        totalDuration = findViewById(R.id.totalDuration);

        databaseHelper = new DatabaseHelper(this);

        int totalRecordsFromDB = databaseHelper.countRecords();
        int totalDurationFromDB = databaseHelper.totalDuration();

        totalRecords.setText(totalRecords.getText().toString() + totalRecordsFromDB);
        totalDuration.setText(totalDuration.getText().toString() + totalDurationFromDB);

    }
}
package com.example.group03_voicerecorder_mobile.app.statistics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

public class StatisticsActivity extends AppCompatActivity {
    private TextView totalRecords;
    private TextView totalDuration;
    private ImageButton btnBack;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utilities.setCustomTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        totalRecords = findViewById(R.id.totalRecords);
        totalDuration = findViewById(R.id.totalDuration);
        btnBack = findViewById(R.id.btnBack);

        databaseHelper = new DatabaseHelper(this);

        int totalRecordsFromDB = databaseHelper.countRecords();
        int totalDurationFromDB = databaseHelper.totalDuration();

        totalRecords.setText(totalRecords.getText().toString() + totalRecordsFromDB);
        totalDuration.setText(totalDuration.getText().toString() + totalDurationFromDB);
        btnBack.setOnClickListener(v-> getOnBackPressedDispatcher().onBackPressed());
    }
}
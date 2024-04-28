package com.example.group03_voicerecorder_mobile.app.statistics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

public class StatisticsActivity extends AppCompatActivity {
    private TextView totalRecords;
    private TextView totalDuration;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String selectedTheme = PreferenceHelper.getSelectedTheme(this, "selectedTheme");
        switch (selectedTheme) {
            case GlobalConstants.THEME_BLUE:
            {
                setTheme(R.style.AppTheme_Blue);
                break;
            }
            case GlobalConstants.THEME_TEAL:
            {
                setTheme(R.style.AppTheme_Teal);
                break;
            }
            case GlobalConstants.THEME_RED:
            {
                setTheme(R.style.AppTheme_Red);
                break;
            }
            case GlobalConstants.THEME_PINK:
            {
                setTheme(R.style.AppTheme_Pink);
                break;
            }
            case GlobalConstants.THEME_PURPLE:
            {
                setTheme(R.style.AppTheme_Purple);
                break;
            }
            case GlobalConstants.THEME_ORANGE:
            {
                setTheme(R.style.AppTheme_DeepOrange);
                break;
            }
            default: {
                setTheme(R.style.AppTheme_Default);
                break;
            }
        }

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
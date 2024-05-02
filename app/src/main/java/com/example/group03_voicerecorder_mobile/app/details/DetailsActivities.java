package com.example.group03_voicerecorder_mobile.app.details;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.record.Record;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import java.util.List;

public class DetailsActivities extends AppCompatActivity {
    private TextView recordingName;
    private TextView recordingFormat;
    private TextView recordingDuration;
    private TextView filePath;
    private TextView created;
    private DatabaseHelper databaseHelper;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Utilities.setCustomTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        databaseHelper = new DatabaseHelper(this);
        recordingName = findViewById(R.id.info_name);
        recordingFormat = findViewById(R.id.info_format);
        recordingDuration = findViewById(R.id.info_duration);
        filePath = findViewById(R.id.info_location);
        created = findViewById(R.id.info_created);

        loadRecordingData();
    }

    private void loadRecordingData() {
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();

        if (bd != null) {
            String name = bd.getString("recordName");
            List<Record> recordList = databaseHelper.getRecordsByFilename(name);
            Record record = recordList.get(0);

            recordingName.setText(record.getFilename());
            recordingFormat.setText(record.getFilePath().substring(record.getFilePath().lastIndexOf('.') + 1));
            recordingDuration.setText(record.getDurationString());
            filePath.setText(record.getFilePath());
            created.setText(record.getTimestampString());
        }
        else {
            Toast.makeText(this, "Could not find record", Toast.LENGTH_LONG).show();
        }
    }
}

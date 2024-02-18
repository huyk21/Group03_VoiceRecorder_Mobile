package com.example.group03_voicerecorder_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    String dateList[] = {"17/02/2024", "18/02/2024", "19/02/2024"};
    String primaryDateList[] = {"17 thg 2", "18 thg 2", "19 thg 2"};
    ListView records;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        records = (ListView) findViewById(R.id.records);
        RecordAdapter recordAdapter = new RecordAdapter(getApplicationContext(), primaryDateList, dateList);
        records.setAdapter(recordAdapter);
    }
}
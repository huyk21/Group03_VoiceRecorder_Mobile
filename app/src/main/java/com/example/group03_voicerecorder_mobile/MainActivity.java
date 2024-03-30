package com.example.group03_voicerecorder_mobile;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.app.record.RecordAdapter;

public class MainActivity extends AppCompatActivity {
    ListView records;
    String[] primaryDateList = {"25/01/2022 05:00"};
    String[] dateList = {"25/01/2002"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        records = (ListView) findViewById(R.id.records);
        RecordAdapter recordAdapter = new RecordAdapter(getApplicationContext(), primaryDateList, dateList);
        records.setAdapter(recordAdapter);
    }

    private void updateFragment(String filter) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", filter);
//        AllRecords newFragment = new AllRecords();
//        newFragment.setArguments(bundle);

    }
}
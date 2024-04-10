package com.example.group03_voicerecorder_mobile.app.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.record.Record;
import com.example.group03_voicerecorder_mobile.app.record.RecordActivity;
import com.example.group03_voicerecorder_mobile.app.record.RecordAdapter;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView records;
    private ImageButton btn_settings;
    private TextView title;
    private EditText searchBar;
    private ImageButton btn_record;
    private DatabaseHelper databaseHelper;
    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        records = (ListView) findViewById(R.id.records);
        btn_settings = (ImageButton) findViewById(R.id.btnToSettings);
        title = (TextView) findViewById(R.id.title);
        searchBar = (EditText) findViewById(R.id.searchBar);
        btn_record = (ImageButton) findViewById(R.id.recordButton);
        databaseHelper = new DatabaseHelper(this);


        // Fetch records from the database
        List<Record> recordList = databaseHelper.getAllRecordings();

        // Check if the recordList is empty
        if (recordList.isEmpty()) {
            Toast.makeText(MainActivity.this, "No records found", Toast.LENGTH_SHORT).show();
        } else {
            // Populate ListView with records
            RecordAdapter recordAdapter = new RecordAdapter(this, recordList);
            records.setAdapter(recordAdapter);
            records.setOnItemClickListener((parent, view, position, id) -> {
                view.setBackgroundResource(R.drawable.list_selector_pressed);
            });
        }

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRecordActivity(v);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Fetch records from the database
        List<Record> recordList = databaseHelper.getAllRecordings();

        // Check if the recordList is empty
        if (recordList.isEmpty()) {
            Toast.makeText(MainActivity.this, "No records found", Toast.LENGTH_SHORT).show();
        } else {
            // Populate ListView with records
            RecordAdapter recordAdapter = new RecordAdapter(this, recordList);
            records.setAdapter(recordAdapter);
            records.setOnItemClickListener((parent, view, position, id) -> {
                view.setBackgroundResource(R.drawable.list_selector_pressed);
            });
        }
    }



    private void showPopupMenu(View view) {
        Toast.makeText(view.getContext(), "click too long", Toast.LENGTH_SHORT);
    }

    private void updateFragment(String filter) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", filter);
//        AllRecords newFragment = new AllRecords();
//        newFragment.setArguments(bundle);
    }

    public void toRecordActivity(View recordView) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    private void addMockDataToDatabase() {
        // Create some dummy records
        Record record1 = new Record(1, "dummy_record_1.mp3", 60000, new Date(), 0);
        Record record2 = new Record(2, "dummy_record_2.mp3", 120000, new Date(), 1);
        Record record3 = new Record(3, "dummy_record_3.mp3", 90000, new Date(), 0);

        // Add the records to the database
        long id1 = databaseHelper.addRecording(record1);
        long id2 = databaseHelper.addRecording(record2);
        long id3 = databaseHelper.addRecording(record3);

        // Check if the records were successfully added
        if (id1 != -1 && id2 != -1 && id3 != -1) {
            // Show a toast message indicating success
            Toast.makeText(MainActivity.this, "Mock data added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Show a toast message indicating failure
            Toast.makeText(MainActivity.this, "Failed to add mock data", Toast.LENGTH_SHORT).show();
        }
    }
}
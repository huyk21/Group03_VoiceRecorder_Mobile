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
            RecordAdapter recordAdapter = new RecordAdapter(getApplicationContext(), recordList);
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

}
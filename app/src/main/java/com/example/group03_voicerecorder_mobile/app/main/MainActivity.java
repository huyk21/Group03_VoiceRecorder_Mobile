package com.example.group03_voicerecorder_mobile.app.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.record.DeletedActivity;
import com.example.group03_voicerecorder_mobile.app.record.Record;
import com.example.group03_voicerecorder_mobile.app.record.RecordActivity;
import com.example.group03_voicerecorder_mobile.app.record.RecordAdapter;
import com.example.group03_voicerecorder_mobile.app.settings.SettingsActivity;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView records;
    private ImageButton btn_more;
    private TextView title;
    private EditText searchBar;
    private ImageButton btn_record;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        records = findViewById(R.id.records);
        btn_more = findViewById(R.id.btnMore);
        title = findViewById(R.id.title);
        searchBar = findViewById(R.id.searchBar);
        btn_record = findViewById(R.id.recordButton);
        databaseHelper = new DatabaseHelper(this);


        // Fetch records from the database
        List<Record> recordList = databaseHelper.getAllUndeletedRecords();
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

        if (PreferenceHelper.getAutoRecord(this)) {
            startRecording();
        }

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRecordActivity(v);
            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                fetchAndPopulateRecords(query);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Fetch records from the database
        List<Record> recordList = databaseHelper.getAllUndeletedRecords();

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
    @SuppressLint("ResourceType")
    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.layout.options_more_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
//            System.out.println(item.getTitle());
            switch (item.getTitle().toString()) {
                case "Settings":
                    toSettingsActivity();
                    return true;
                case "Recycle Bin":
                    toRecycleBinActivity();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    public void toRecycleBinActivity() {
        Intent intent = new Intent(this, DeletedActivity.class);
        startActivity(intent);
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

    public void toSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void fetchAndPopulateRecords(String query) {
        // Fetch records from the database based on the query
        List<Record> recordList;
        if (query.isEmpty()) {
            recordList = databaseHelper.getAllUndeletedRecords();
        } else {
            recordList = databaseHelper.getRecordsByFilename(query);
        }

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

    private void startRecording() {
        Intent intent = new Intent(this, RecordActivity.class);
        startService(intent);
    }
}
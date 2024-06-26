package com.example.group03_voicerecorder_mobile.app.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.record.AudioListener;
import com.example.group03_voicerecorder_mobile.app.record.DeletedActivity;
import com.example.group03_voicerecorder_mobile.app.record.Record;
import com.example.group03_voicerecorder_mobile.app.record.RecordActivity;
import com.example.group03_voicerecorder_mobile.app.record.RecordAdapter;
import com.example.group03_voicerecorder_mobile.app.record.ScheduleService;
import com.example.group03_voicerecorder_mobile.app.settings.SettingsActivity;
import com.example.group03_voicerecorder_mobile.app.welcome.WelcomeActivity;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView records;
    private ImageButton btn_more;
    private TextView title;
    private EditText searchBar;
    private ImageButton btn_record;
    private DatabaseHelper databaseHelper;
    private AudioListener audioListener;
    private Button btnSelectAllOrDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utilities.setCustomTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        records = findViewById(R.id.records);
        btn_more = findViewById(R.id.btnMore);
        title = findViewById(R.id.title);
        searchBar = findViewById(R.id.searchBar);
        btn_record = findViewById(R.id.recordButton);
        databaseHelper = new DatabaseHelper(this);
        btnSelectAllOrDelete = findViewById(R.id.btnSelectAllOrDelete);
        if (isFirstTime()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }

        if (PreferenceHelper.loadSettingsState(this, "isAutoRecord")) { initAutoRecord(); }
        if (!PreferenceHelper.getSelectedDate(this, "selectedDateTime").equals("")) {
            Intent intent = new Intent(this, ScheduleService.class);
            startService(intent);
        }
        // Fetch records from the database
        List<Record> recordList = databaseHelper.getAllUndeletedRecords();
        RecordAdapter recordAdapter = new RecordAdapter(this, recordList);
        records.setAdapter(recordAdapter);

        setupSelectAllDeleteButton(recordAdapter);
        records.setOnItemClickListener((parent, view, position, id) -> {
            view.setBackgroundResource(R.drawable.list_selector_pressed);
        });

        btn_record.setOnClickListener(this::toRecordActivity);

        btn_more.setOnClickListener(this::showPopupMenu);
        
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

    private void setupSelectAllDeleteButton(RecordAdapter adapter) {
        Button btnSelectAllOrDelete = findViewById(R.id.btnSelectAllOrDelete);
        btnSelectAllOrDelete.setOnClickListener(v -> {
            if ("Select All".equals(btnSelectAllOrDelete.getText().toString())) {
                adapter.selectAllRecords();
                btnSelectAllOrDelete.setText("Delete Selected");
            } else {
                // Prompt for confirmation before deletion
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Do you really want to delete all selected records?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            adapter.deleteSelectedRecords();
                            records.setAdapter(null);
                            records.setAdapter(adapter);

                            btnSelectAllOrDelete.setText("Select All");


                            // Check if all items are removed or update accordingly
                            if (adapter.getCount() == 0) {
                                Toast.makeText(this, "All records deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }



    private void initAutoRecord() {
        audioListener = new AudioListener(this);

        if (checkAudioPermissions()) {
            audioListener.startListening();
        }
    }

    private boolean checkAudioPermissions() {
        if (this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
            return false;
        }
        return true;
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }
        return isFirstRun;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Fetch records from the database
        List<Record> recordList = databaseHelper.getAllUndeletedRecords();

        // Check if the recordList is empty
        if (!recordList.isEmpty()) {
            // Populate ListView with records
            RecordAdapter recordAdapter = new RecordAdapter(this, recordList);
            records.setAdapter(recordAdapter);
            setupSelectAllDeleteButton(recordAdapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioListener != null) {
            audioListener.stopListening();
        }
    }
}
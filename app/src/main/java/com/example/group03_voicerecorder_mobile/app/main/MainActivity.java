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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.record.AudioListener;
import com.example.group03_voicerecorder_mobile.app.record.DeletedActivity;
import com.example.group03_voicerecorder_mobile.app.record.Record;
import com.example.group03_voicerecorder_mobile.app.record.RecordActivity;
import com.example.group03_voicerecorder_mobile.app.record.RecordAdapter;
import com.example.group03_voicerecorder_mobile.app.settings.SettingsActivity;
import com.example.group03_voicerecorder_mobile.app.welcome.WelcomeActivity;
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
    private AudioListener audioListener;


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
        setContentView(R.layout.activity_main);

        records = findViewById(R.id.records);
        btn_more = findViewById(R.id.btnMore);
        title = findViewById(R.id.title);
        searchBar = findViewById(R.id.searchBar);
        btn_record = findViewById(R.id.recordButton);
        databaseHelper = new DatabaseHelper(this);

        if (isFirstTime()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }

        if (PreferenceHelper.loadSettingsState(this, "isAutoRecord")) { initAutoRecord(); }

        // Fetch records from the database
        List<Record> recordList = databaseHelper.getAllUndeletedRecords();
        if (!recordList.isEmpty()) {
            // Populate ListView with records
            RecordAdapter recordAdapter = new RecordAdapter(this, recordList);
            records.setAdapter(recordAdapter);
            records.setOnItemClickListener((parent, view, position, id) -> {
                view.setBackgroundResource(R.drawable.list_selector_pressed);
            });
        }

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
package com.example.group03_voicerecorder_mobile.app.record;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

import java.util.List;

public class DeletedActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private TextView title;
    private ImageButton btnDeleteAll;
    private EditText searchBar;
    private DatabaseHelper dbHelper;
    private ListView records;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_recycle_bin);

        backBtn = findViewById(R.id.backButton);
        title = findViewById(R.id.title);
        btnDeleteAll = findViewById(R.id.deleteAll);
        searchBar = findViewById(R.id.searchBar);
        dbHelper = new DatabaseHelper(this);
        records = findViewById(R.id.scrollList);

        setupBtnListeners();
        // Fetch records from the database
        List<Record> recordList = dbHelper.getAllDeletedRecords();
        if (recordList.isEmpty()) {
            Toast.makeText(DeletedActivity.this, "No records found", Toast.LENGTH_SHORT).show();
        } else {
            // Populate ListView with records
            DeletedRecordsAdapter deletedRecordsAdapter = new DeletedRecordsAdapter(this, recordList);
            records.setAdapter(deletedRecordsAdapter);
            records.setOnItemClickListener((parent, view, position, id) -> {
                view.setBackgroundResource(R.drawable.list_selector_pressed);
            });
        }
    }

    public void setupBtnListeners() {
        backBtn.setOnClickListener(v -> {
            finish();
        });

        btnDeleteAll.setOnClickListener(v -> {

        });
    }
}

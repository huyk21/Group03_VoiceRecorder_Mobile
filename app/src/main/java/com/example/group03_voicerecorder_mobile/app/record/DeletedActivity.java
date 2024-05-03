package com.example.group03_voicerecorder_mobile.app.record;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import java.io.File;
import java.util.List;

public class DeletedActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private TextView title;
    private ImageButton btnDeleteAll;
    private EditText searchBar;

    private ListView records;
    private static DeletedRecordsAdapter deletedRecordsAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Utilities.setCustomTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_bin);

        backBtn = findViewById(R.id.backButton);
        title = findViewById(R.id.title);
        btnDeleteAll = findViewById(R.id.deleteAll);
        searchBar = findViewById(R.id.searchBar);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        records = findViewById(R.id.scrollList);


        // Fetch records from the database
        List<Record> recordList = dbHelper.getAllDeletedRecords();
        if (recordList.isEmpty()) {
            Toast.makeText(DeletedActivity.this, "No records found", Toast.LENGTH_SHORT).show();
        } else {
            // Populate ListView with records
            Toast.makeText(this, recordList.size() + " records found", Toast.LENGTH_SHORT).show();
            deletedRecordsAdapter = new DeletedRecordsAdapter(this, recordList);

            records.setAdapter(deletedRecordsAdapter);
            records.setOnItemClickListener((parent, view, position, id) -> {
                view.setBackgroundResource(R.drawable.list_selector_pressed);
            });
        }
        setupBtnListeners();
    }

    public void setupBtnListeners() {
        backBtn.setOnClickListener(v -> {
            finish();
        });

        btnDeleteAll.setOnClickListener(v -> deleteAllPermanently());
    }

    public void deletePermanently(int position) {
        Record record = (Record) deletedRecordsAdapter.getItem(position);
        int recordId = (int) deletedRecordsAdapter.getItemId(position);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.deleteRecording(recordId);
        String fileName = "amplitudes_" + recordId + ".json";
        String filePath = record.getFilePath();
        Utilities.deleteFile(filePath,this);
        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        if (file.exists() && file.delete()) {
            Toast.makeText(this, "Record deleted permanently", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error deleting record permanently", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteAllPermanently() {

        // Deleting from the end to the start to avoid index shifting issues
        for (int i = deletedRecordsAdapter.getCount() - 1; i >= 0; i--) {

            deletePermanently(i);
        }

        // Clear adapter data and update UI
        deletedRecordsAdapter.clearRecords(); // Ensure your adapter has a method or direct access to clear records
        deletedRecordsAdapter.notifyDataSetChanged();

        Toast.makeText(this, "All records deleted permanently", Toast.LENGTH_SHORT).show();
    }


}

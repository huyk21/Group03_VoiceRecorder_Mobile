package com.example.group03_voicerecorder_mobile.app.main;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.record.RecordAdapter;

public class MainActivity extends AppCompatActivity {
    private ListView records;
    private ImageButton btn_settings;
    private TextView title;
    private EditText searchBar;
    private ImageButton btn_record;
    String[] primaryDateList = {"25/01/2022", "29/02/2023"};
    String[] nameList = {"Record-01","Record-02" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        records = (ListView) findViewById(R.id.records);
        btn_settings = (ImageButton) findViewById(R.id.btnToSettings);
        title = (TextView) findViewById(R.id.title);
        searchBar = (EditText) findViewById(R.id.searchBar);
        btn_record = (ImageButton) findViewById(R.id.recordButton);

        RecordAdapter recordAdapter = new RecordAdapter(getApplicationContext(), primaryDateList, nameList);
        records.setAdapter(recordAdapter);
        records.setOnItemClickListener((parent, view, position, id) -> {
            view.setBackgroundResource(R.drawable.list_selector_pressed);
        });    }

    private void showPopupMenu(View view) {
        Toast.makeText(view.getContext(), "click too long", Toast.LENGTH_SHORT);
    }

    private void updateFragment(String filter) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", filter);
//        AllRecords newFragment = new AllRecords();
//        newFragment.setArguments(bundle);
    }
}
package com.example.group03_voicerecorder_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group03_voicerecorder_mobile.app.record.RecordAdapter;

public class MainActivity extends AppCompatActivity {
    ListView records;
    String[] primaryDateList = {"25/01/2022", "25/02/2023"};
    String[] dateList = {"25/01/2002", "25/02"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        records = (ListView) findViewById(R.id.records);
        RecordAdapter recordAdapter = new RecordAdapter(getApplicationContext(), primaryDateList, dateList);
        records.setAdapter(recordAdapter);

        records.setLongClickable(true);
        records.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Show the popup window
                showPopupWindow(view);
                return true; // Return true to indicate that the event was handled
            }
        });


    }

    private void showPopupMenu(View view) {
        Toast.makeText(this, "click too long", Toast.LENGTH_SHORT);
    }

    private void updateFragment(String filter) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", filter);
//        AllRecords newFragment = new AllRecords();
//        newFragment.setArguments(bundle);
    }
    private void showPopupWindow(View anchorView) {
        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.record_item_popup, null);

        // Create the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Customize the popup content (e.g., set text for TextView, handle button clicks)

        // Show the popup window at a specific location
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

}
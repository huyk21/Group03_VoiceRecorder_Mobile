package com.example.group03_voicerecorder_mobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.group03_voicerecorder_mobile.R; // Assuming your resource file is named R
import com.example.group03_voicerecorder_mobile.data.MockRecordings;
import com.example.group03_voicerecorder_mobile.data.RecordingInfo;

import java.util.ArrayList;
import java.util.List;

public class AllRecords extends Fragment {

    private ListView recordingList;
    private String currentFilter = "all";
    private List<RecordingInfo> mockRecordings;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MockRecordings mockRecordingsProvider = new MockRecordings();
        mockRecordings = mockRecordingsProvider.getMockRecordings();
        if (getArguments() != null) {
            String filter = getArguments().getString("filter");
            updateRecordings(filter);
        } else {
            currentFilter = "all";
        }
    }

    private void updateRecordings(String filter) {
        MockRecordings mockRecordingsProvider = new MockRecordings();
        List<RecordingInfo> result = new ArrayList<>();
        List<RecordingInfo> allRecordings = mockRecordingsProvider.getMockRecordings();
        for (RecordingInfo record : allRecordings) {
            if (record.getType().equals(filter)) {
                result.add(record);
            }
        }
        mockRecordings = result;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        // Find the ListView element
            recordingList = view.findViewById(R.id.records); // Assuming your ListView has this ID

        // (Optional) Prepare your list of recording data (replace with your logic)
            String[] recordings = {"Recording 1", "Recording 2", "Recording 3"};

        // Create an ArrayAdapter to display the recordings in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, recordings);

        // Set the adapter for the ListView
        recordingList.setAdapter(adapter);

        return view;
    }
}

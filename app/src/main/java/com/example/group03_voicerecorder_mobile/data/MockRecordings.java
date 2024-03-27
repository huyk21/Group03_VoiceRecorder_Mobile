package com.example.group03_voicerecorder_mobile.data;

import java.util.ArrayList;
import java.util.List;

public class MockRecordings {

    public List<RecordingInfo> getMockRecordings() {
        List<RecordingInfo> recordingList = new ArrayList<>();

        recordingList.add(new RecordingInfo("Meeting Recording", "00:32:15", "2024-03-19", "record"));
        recordingList.add(new RecordingInfo("Lecture Notes", "01:10:05", "2024-03-18", "record"));
        recordingList.add(new RecordingInfo("Important Call", "00:05:23", "2024-03-18", "call"));
        recordingList.add(new RecordingInfo("Voice Memo", "00:01:10", "2024-03-17", "call"));

        return recordingList;
    }
}


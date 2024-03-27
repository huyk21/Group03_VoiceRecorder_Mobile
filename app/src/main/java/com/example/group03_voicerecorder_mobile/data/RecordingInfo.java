package com.example.group03_voicerecorder_mobile.data;

public class RecordingInfo {
    private String name;
    private String duration;
    private String dateCreated;
    private String type;

    public RecordingInfo(String name, String duration, String dateCreated, String type) {
        this.name = name;
        this.duration = duration;
        this.dateCreated = dateCreated;
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

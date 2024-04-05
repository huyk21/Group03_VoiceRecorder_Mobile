package com.example.group03_voicerecorder_mobile.app.record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Record {
    private String filename;
    private String filePath;
    private long durationMillis;
    private Date timestamp;
    public Record() {
        this.filename = "";
        this.filePath = "";
        this.durationMillis = 0;
        this.timestamp = new Date();
    }

    public Record(String filename, long durationMillis, Date timestamp) {
        this.filename = filename;
        this.durationMillis = durationMillis;
        this.timestamp = timestamp;
    }

    public Record(String filename, String filePath, long durationMillis, Date timestamp) {
        this.filename = filename;
        this.filePath = filePath;
        this.durationMillis = durationMillis;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getDurationString() {
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;

        return String.format("%02d:%02d", hours, minutes);
    }
    public String getTimestampString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Format the timestamp using the specified pattern
        String formattedDate = dateFormat.format(this.timestamp);
        return formattedDate;
    }
}


package com.example.group03_voicerecorder_mobile.app.record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Record {
    private Integer id;
    private String filename;
    private String filePath;
    private long durationMillis;
    private Date timestamp;
    private boolean bookmarked;
    public Record() {
        this.filename = "";
        this.filePath = "";
        this.durationMillis = 0;
        this.timestamp = new Date();
        this.bookmarked = false;
    }

    public Record(String filename, long durationMillis, Date timestamp) {
        this.filename = filename;
        this.durationMillis = durationMillis;
        this.timestamp = timestamp;
    }

    public Record(Integer id, String filename, long durationMillis, Date timestamp) {
        this.id = id;
        this.filename = filename;
        this.durationMillis = durationMillis;
        this.timestamp = timestamp;
    }

    public Record(String filename, long elapsedMillis, Date timestamp, boolean bookmarked) {
        this.filename = filename;
        this.durationMillis = elapsedMillis;
        this.timestamp = timestamp;
        this.bookmarked = bookmarked;
    }

    // Getters and setters
    public String getFilename() {
        return filename;
    }

    public Integer getId() {
        return id;
    }

    public boolean isBookmarked() {
        return bookmarked;
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

    public void setBookmarked(boolean bookmarked) {
    }
}


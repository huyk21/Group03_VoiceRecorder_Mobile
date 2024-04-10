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
    private int bookmarked;
    public Record() {
        this.filename = "";
        this.filePath = "";
        this.durationMillis = 0;
        this.timestamp = new Date();
        this.bookmarked = 0;
    }

    public Record(String filename, long durationMillis, Date timestamp) {
        this.filename = filename;
        this.durationMillis = durationMillis;
        this.timestamp = timestamp;
    }

    public Record(int id, String filename, long duration, Date timestamp, int bookmarked) {
        this.id = id;
        this.filename = filename;
        this.durationMillis = duration;
        this.timestamp = timestamp;
        this.bookmarked = bookmarked;
    }

    public Record(String filename, long elapsedMillis, Date timestamp, int bookmarked) {
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

    public int getBookmarked() {
        return bookmarked;
    }
    public void setBookmarked(int bookmarked) {
        this.bookmarked = bookmarked;
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
        long hours = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
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


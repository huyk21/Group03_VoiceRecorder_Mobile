package com.example.group03_voicerecorder_mobile.app.record;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Record implements Serializable {
    private Integer id;
    private String filename;
    private String filePath;
    private long durationMillis;
    private Date timestamp;
    private int bookmarked;
    private int deleted;
    private ArrayList<Integer> amplitudes;
    private boolean isSelected;
    // existing properties like id, filename, etc.

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public Record() {
        this.filename = "";
        this.filePath = "";
        this.durationMillis = 0;
        this.timestamp = new Date();
        this.bookmarked = 0;
        this.deleted = 0;
        this.isSelected = false;
    }
    // Getter and Setter
    public ArrayList<Integer> getAmplitudes() {
        return amplitudes;
    }

    public void setAmplitudes(ArrayList<Integer> amplitudes) {
        this.amplitudes = amplitudes;
    }
    public void setId(Integer id) {
        this.id = id;
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

    public Record(int id, String filename, long duration, Date date, int bookmarked, int deletedValue, String filePath) {
        this.id = id;
        this.filename = filename;
        this.durationMillis = duration;
        this.timestamp = timestamp;
        this.bookmarked = bookmarked;
        this.deleted = deleted;
        this.filePath = filePath;
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
    public int getDeleted(){return deleted; }
    public void setDeleted(int deleted) {this.deleted = deleted;}
    public String getDurationString() {
        long hours = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
        return String.format("%02d:%02d", hours, minutes);
    }
    public String getTimestampString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        // Format the timestamp using the specified pattern
        if (this.timestamp != null) {
            String formattedDate = dateFormat.format(this.timestamp);
            return formattedDate;
        }
        else
            return "Not";
    }

    public void setBookmarked(boolean bookmarked) {
    }
}


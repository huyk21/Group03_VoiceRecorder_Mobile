package com.example.group03_voicerecorder_mobile.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.group03_voicerecorder_mobile.app.record.Record;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "RecorderDB";

    // Table name
    private static final String TABLE_RECORDINGS = "recordings";

    // Columns
    private static final String KEY_ID = "id";
    private static final String KEY_FILENAME = "filename";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_BOOKMARKED = "bookmarked";
    private static final String KEY_DELETED = "deleted";
    private static final String KEY_FILEPATH = "filepath";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Modified table creation SQL statement to include the new column for filepath
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RECORDINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FILENAME + " TEXT,"
                + KEY_DURATION + " INTEGER,"
                + KEY_TIMESTAMP + " TEXT,"
                + KEY_BOOKMARKED + " INTEGER DEFAULT 0,"
                + KEY_DELETED + " INTEGER DEFAULT 0,"
                + KEY_FILEPATH + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDINGS);
        onCreate(db);
    }

    // Create operation
    public long addRecording(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String fileName = record.getFilename();
        values.put(KEY_FILENAME, fileName);
        values.put(KEY_DURATION, record.getDurationMillis());
        values.put(KEY_TIMESTAMP, record.getTimestampString());
        values.put(KEY_BOOKMARKED, record.getBookmarked()); // Add the bookmarked field
        values.put(KEY_FILEPATH, record.getFilePath()); // Add the filepath
        long id = db.insert(TABLE_RECORDINGS, null, values);
        db.close();
        return id;
    }

    // Read operation
    // Method to retrieve all deleted records from the database
    public List<Record> getAllDeletedRecords() {
        return getAllRecords(true);
    }

    // Method to retrieve all undeleted records from the database
    public List<Record> getAllUndeletedRecords() {
        return getAllRecords(false);
    }

    // Helper method to retrieve records based on the deleted state
    private List<Record> getAllRecords(boolean deleted) {
        List<Record> recordingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_RECORDINGS + " WHERE " + KEY_DELETED + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{deleted ? "1" : "0"});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                        String filename = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(KEY_DURATION));
                        String timestamp = cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP));
                        int bookmarked = cursor.getInt(cursor.getColumnIndex(KEY_BOOKMARKED));
                        int deletedValue = cursor.getInt(cursor.getColumnIndex(KEY_DELETED));
                        String filePath = cursor.getString(cursor.getColumnIndex(KEY_FILEPATH));
                        String pattern = "dd/MM/yyyy"; // Date format pattern
                        Date date = Utilities.stringToDate(timestamp, pattern);
                        if (date == null) continue;

                        Record recording = new Record(id, filename, duration, date, bookmarked, deletedValue, filePath);
                        recording.setTimestamp(Utilities.stringToDate(timestamp, pattern));
                        recordingList.add(recording);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        db.close();

        return recordingList;
    }
    public List<Record> getRecordsByFilename(String filename) {
        List<Record> recordingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_RECORDINGS + " WHERE " + KEY_FILENAME + " LIKE ? AND " + KEY_DELETED + " = 0";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + filename + "%"});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                        String recordFilename = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(KEY_DURATION));
                        String timestamp = cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP));
                        int bookmarked = cursor.getInt(cursor.getColumnIndex(KEY_BOOKMARKED));
                        int deletedValue = cursor.getInt(cursor.getColumnIndex(KEY_DELETED)); // Retrieve the deleted field
                        String filePath = cursor.getString(cursor.getColumnIndex(KEY_FILEPATH));
                        String pattern = "dd/MM/yyyy"; // Date format pattern
                        Date date = Utilities.stringToDate(timestamp, pattern);
                        if (date == null) continue;

                        // Modify the constructor call to include the deleted field
                        Record recording = new Record(id, recordFilename, duration, date, bookmarked, deletedValue, filePath);
                        recordingList.add(recording);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        db.close();

        return recordingList;
    }
    // Update operation
    public int updateFileName(Integer recordId, String filename) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FILENAME, filename);
        return db.update(TABLE_RECORDINGS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(recordId)});
    }

    // Delete operation
    public void deleteRecording(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDINGS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
    public int updateBookmarkState(long recordId, int bookmarkedState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOKMARKED, bookmarkedState);
        return db.update(TABLE_RECORDINGS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(recordId)});
    }

    public int updateDeletedState(long recordId, int deletedState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DELETED, deletedState);
        return db.update(TABLE_RECORDINGS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(recordId)});
    }
}

package com.example.group03_voicerecorder_mobile.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.group03_voicerecorder_mobile.app.record.Record;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    // Database Version
    private static final int DATABASE_VERSION = 1;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RECORDINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FILENAME + " TEXT,"
                + KEY_DURATION + " INTEGER,"
                + KEY_TIMESTAMP + " TEXT,"  // Store date as string
                + KEY_BOOKMARKED + " INTEGER DEFAULT 0"  // 0 for false, 1 for true
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDINGS);
        onCreate(db);
    }

    // Create operation
    public long addRecording(String filename, long duration, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FILENAME, filename);
        values.put(KEY_DURATION, duration);
        values.put(KEY_TIMESTAMP, timestamp);
        long id = db.insert(TABLE_RECORDINGS, null, values);
        db.close();
        return id;
    }

    // Read operation
    public List<Record> getAllRecordings() {
        List<Record> recordingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RECORDINGS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                        String filename = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(KEY_DURATION));
                        long timestampMillis = cursor.getColumnIndex(KEY_TIMESTAMP);

                        Date timestamp = null;
                        if (!cursor.isNull(cursor.getColumnIndex(KEY_TIMESTAMP))) {
                            timestamp = new Date(timestampMillis);
                        }

                        Record recording = new Record(id, filename, duration, timestamp);
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
}

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

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    // Database Version
    private static final int DATABASE_VERSION = 1;
    private boolean bookmarked;
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

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECORDINGS_TABLE = "CREATE TABLE " + TABLE_RECORDINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FILENAME + " TEXT,"
                + KEY_DURATION + " INTEGER,"
                + KEY_TIMESTAMP + " TEXT,"
                + KEY_BOOKMARKED + " INTEGER DEFAULT 0"  // 0 for false, 1 for true
                + ")";
        db.execSQL(CREATE_RECORDINGS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDINGS);
        onCreate(db);
    }

    public long addRecording(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FILENAME, record.getFilename());
        values.put(KEY_DURATION, record.getDurationMillis());
        values.put(KEY_TIMESTAMP, dateFormat.format(record.getTimestamp()));
        values.put(KEY_BOOKMARKED, record.isBookmarked() ? 1 : 0);

        long id = db.insert(TABLE_RECORDINGS, null, values);
        db.close();
        return id;
    }

    public List<Record> getAllRecordings() {
        List<Record> recordingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RECORDINGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int filenameIndex = cursor.getColumnIndex(KEY_FILENAME);
                int durationIndex = cursor.getColumnIndex(KEY_DURATION);
                int timestampIndex = cursor.getColumnIndex(KEY_TIMESTAMP);
                int bookmarkedIndex = cursor.getColumnIndex(KEY_BOOKMARKED);
                if (idIndex != -1 && filenameIndex != -1 && durationIndex != -1 &&
                        timestampIndex != -1 && bookmarkedIndex != -1) {

                    int id = cursor.getInt(idIndex);
                    String filename = cursor.getString(filenameIndex);
                    long duration = cursor.getLong(durationIndex);
                    String timestampString = cursor.getString(timestampIndex);
                    Date timestamp = null;
                    try {
                        if (timestampString != null) {
                            timestamp = dateFormat.parse(timestampString);
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Date parsing error", e);
                    }
                    boolean bookmarked = cursor.getInt(bookmarkedIndex) == 1;

                    Record recording = new Record(id, filename, duration, timestamp);
                    recording.setBookmarked(bookmarked);
                    recordingList.add(recording);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();

        return recordingList;
    }

    public int updateFileName(int recordId, String filename) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FILENAME, filename);
        int rowsAffected = db.update(TABLE_RECORDINGS, values, KEY_ID + "=?", new String[]{String.valueOf(recordId)});
        db.close();
        return rowsAffected;
    }

    public void deleteRecording(int recordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDINGS, KEY_ID + "=?", new String[]{String.valueOf(recordId)});
        db.close();
    }
    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }
}

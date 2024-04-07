package com.example.group03_voicerecorder_mobile.audio.recorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.record.Record;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.exception.DatabaseError;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class RecordService extends Service {

    MediaRecorder mediaRecorder;
    long startTimeMillis = 0;
    long elapsedTimeMillis = 0;
    File file;
    String fileName;
    DatabaseHelper dbHelper;
    boolean isPaused = false;
    boolean isResumed = false;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(getApplicationContext());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            isPaused = intent.getExtras().getBoolean("com.example.group03_voicerecorder_mobile.pauseFlag");
            isResumed = intent.getExtras().getBoolean("com.example.group03_voicerecorder_mobile.resumeFlag");
            if (isPaused) {
                onPause();
            }
            else if (isResumed)
            {
                onResume();
            }
            else startRecording();
        }
        catch(NullPointerException npe) {
            npe.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    public void onPause() {
        if (mediaRecorder != null)
            mediaRecorder.pause();
        else System.out.println("err");
    }

    public void onResume() {
        if (mediaRecorder != null)
            mediaRecorder.resume();
        else System.out.println("err");
    }

    private void startRecording() {
        long tsLong = System.currentTimeMillis()/1000;
        String ts = Long.toString(tsLong);

        fileName = GlobalConstants.DEFAULT_RECORD_NAME + ts;
        file = new File(Environment.getExternalStorageDirectory() + GlobalConstants.STORAGE_DIR + fileName + GlobalConstants.FORMAT_MP3);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

            startTimeMillis = System.currentTimeMillis();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private void stopRecording() {
        mediaRecorder.stop();
        elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        mediaRecorder.release();

        //add to database
        Record record = new Record(fileName + GlobalConstants.FORMAT_MP3, elapsedTimeMillis, new Date());
        long id = dbHelper.addRecording(record);
        Toast.makeText(getApplicationContext(), "Recording successfully saved to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        if (id == -1) {
            Log.e("Database Exception", "DB Write Exception");
        }
    }
}

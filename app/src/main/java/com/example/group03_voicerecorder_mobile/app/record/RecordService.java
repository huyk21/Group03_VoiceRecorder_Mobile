package com.example.group03_voicerecorder_mobile.app.record;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class RecordService extends Service {
    private MediaRecorder mediaRecorder;
    private String currentFilePath;
    private boolean isRecording = false;
    private long timeWhenPaused = 0;
    private Handler handler = new Handler();
    private ArrayList<Integer> amplitudeList = new ArrayList<>();
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("ACTION_START_RECORDING".equals(intent.getAction())) {
            startRecording();
        } else if ("ACTION_STOP_RECORDING".equals(intent.getAction())) {
            stopRecording();
        } else if ("ACTION_PAUSE_RECORDING".equals(intent.getAction())) {
            pauseRecording();
        } else if ("ACTION_RESUME_RECORDING".equals(intent.getAction())) {
            resumeRecording();
        }
        return START_STICKY;
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioChannels(1);
        currentFilePath = getExternalFilesDir(null).getAbsolutePath() + "/" + GlobalConstants.DEFAULT_RECORD_NAME + " " + System.currentTimeMillis() / 1000 + GlobalConstants.FORMAT_M4A;
        mediaRecorder.setOutputFile(currentFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            handler.post(updateAmplitudeTask);
            Log.i("RecordService", "Recording started");
        } catch (Exception e) {
            Toast.makeText(this, "Recording failed to start", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            long elapsedMillis = SystemClock.elapsedRealtime() - timeWhenPaused;
            saveAmplitudesToJson(new File(currentFilePath).getName(), amplitudeList);
            Log.i("RecordService", "Recording stopped");
        }
    }

    private void pauseRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.pause();
            isRecording = false;
            timeWhenPaused = SystemClock.elapsedRealtime();
            handler.removeCallbacks(updateAmplitudeTask);
            Log.i("RecordService", "Recording paused");
        }
    }

    private void resumeRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.resume();
            isRecording = true;
            handler.post(updateAmplitudeTask);
            Log.i("RecordService", "Recording resumed");
        }
    }

    private Runnable updateAmplitudeTask = new Runnable() {
        @Override
        public void run() {
            if (isRecording && mediaRecorder != null) {
                int amplitude = mediaRecorder.getMaxAmplitude();
                amplitudeList.add(amplitude);
                handler.postDelayed(this, 100);
            }
        }
    };

    private void saveAmplitudesToJson(String fileName, ArrayList<Integer> amplitudes) {
        try {
            JSONObject json = new JSONObject();
            json.put("amplitudes", new JSONArray(amplitudes));

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "amplitudes_" + fileName + ".json");
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(json.toString());
                Log.i("RecordService", "Amplitudes saved to " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save amplitudes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("RecordService", "Error saving amplitudes", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }
}

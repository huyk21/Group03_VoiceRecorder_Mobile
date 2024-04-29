package com.example.group03_voicerecorder_mobile.app.record;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

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
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "RecordServiceChannel";
    public static final String RECORDING_STATUS_UPDATE = "recording_status_update";


    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, RecordActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, RecordService.class);
        stopIntent.setAction("ACTION_STOP_RECORDING");
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Recording...")
                .setContentText("Tap to return to the recording.")
                .setSmallIcon(R.drawable.ic_record)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_stop_red, "Stop", stopPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY; // or START_REDELIVER_INTENT if you need the Intent redelivered
        }
        if ("ACTION_START_RECORDING".equals(intent.getAction())) {
            startForegroundService();
            startRecording();
        } else if ("ACTION_STOP_RECORDING".equals(intent.getAction())) {
            stopRecording();
            stopForeground(true);
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
        timeWhenPaused = SystemClock.elapsedRealtime();

        long startTime = SystemClock.elapsedRealtime();
        PreferenceHelper.saveElapsedTime(this, "ElapsedTime", startTime);

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
            PreferenceHelper.deleteElapsedTime(this, "ElapsedTime");
            long elapsedMillis = SystemClock.elapsedRealtime() - timeWhenPaused;
            String fileName = currentFilePath.substring(currentFilePath.lastIndexOf('/') + 1,
                    currentFilePath.lastIndexOf('.'));
            Record record = new Record();
            record.setFilePath(currentFilePath);
            record.setDurationMillis(elapsedMillis);
            record.setFilename(fileName);

            long newRowId = databaseHelper.addRecording(record);
            saveAmplitudesToJson(newRowId, amplitudeList);

            if (newRowId != -1) {
                Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save recording.", Toast.LENGTH_SHORT).show();
            }
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
                Intent broadcastIntent = new Intent(RECORDING_STATUS_UPDATE);
                broadcastIntent.putExtra("recording_status", amplitude);
                getApplicationContext().sendBroadcast(broadcastIntent);
                amplitudeList.add(amplitude);
                handler.postDelayed(this, 100);
            }
        }
    };

    private void saveAmplitudesToJson(long recordId, ArrayList<Integer> amplitudes) {
        try {
            // Create a JSON object to hold the data
            JSONObject json = new JSONObject();
            json.put("recordId", recordId);

            // Convert amplitudes ArrayList to JSONArray
            JSONArray jsonAmplitudes = new JSONArray(amplitudes);
            json.put("amplitudes", jsonAmplitudes);

            // Generate the filename based on record ID
            String fileName = "amplitudes_" + recordId + ".json";

            // Determine where to save the file
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

            // Write JSON string to the file
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(json.toString());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Failed to save amplitudes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Handle exceptions here
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.onDestroy();
    }
}
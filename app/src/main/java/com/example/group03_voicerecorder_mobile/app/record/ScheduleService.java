package com.example.group03_voicerecorder_mobile.app.record;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleService extends Service {
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public ScheduleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // No binding provided
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Recording scheduled.", Toast.LENGTH_SHORT).show();
        try {
            scheduleRecording();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return START_STICKY;
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleRecording() throws ParseException {
        String dateTime= PreferenceHelper.getSelectedDate(this, "selectedDateTime");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Parse the date string into a Date object
        Date date = sdf.parse(dateTime);

        // Get the time in milliseconds since January 1, 1970, 00:00:00 GMT
        assert date != null;
        long timeInMillis = date.getTime();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, RecordService.class);
        intent.setAction("ACTION_START_RECORDING");
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (timeInMillis > System.currentTimeMillis()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else {
            // Handle past date or log error
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}

package com.example.group03_voicerecorder_mobile.app.record;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(PreferenceHelper.loadSettingsState(context, "isPhoneActive")){
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                Toast.makeText(context, "Ringing", Toast.LENGTH_SHORT).show();
                Intent startIntent = new Intent(context, RecordService.class);
                startIntent.setAction("ACTION_START_RECORDING");
                context.startService(startIntent);
            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                Toast.makeText(context, "Off Hook", Toast.LENGTH_SHORT).show();

            } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                Toast.makeText(context, "Idle", Toast.LENGTH_SHORT).show();
                Intent stopIntent = new Intent(context, RecordService.class);
                stopIntent.setAction("ACTION_STOP_RECORDING");
                context.startService(stopIntent);
            }
        }

    }
}

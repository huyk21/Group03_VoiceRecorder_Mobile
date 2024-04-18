package com.example.group03_voicerecorder_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.group03_voicerecorder_mobile.app.GlobalConstants;

public class PreferenceHelper {
    public static void setAutoRecord(Context context, boolean isEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("autoRecord", isEnabled);
        editor.apply();
    }

    public static boolean getAutoRecord(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("autoRecord", false);
    }
}

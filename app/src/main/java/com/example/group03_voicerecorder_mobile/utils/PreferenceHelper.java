package com.example.group03_voicerecorder_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.group03_voicerecorder_mobile.app.GlobalConstants;

public class PreferenceHelper {


    public static void saveSettingsState(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static void removeSetting(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
    public static boolean loadSettingsState(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void saveElapsedTime(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getElapsedTime(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }

    public static void deleteElapsedTime(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void saveSelectedFormat(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSelectedFormat(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(key, GlobalConstants.FORMAT_M4A);
    }
    public static void saveSelectedTheme(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSelectedTheme(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(key, GlobalConstants.THEME_DEFAULT);
    }
}

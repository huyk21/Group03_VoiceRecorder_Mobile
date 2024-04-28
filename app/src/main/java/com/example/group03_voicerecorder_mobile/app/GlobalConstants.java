package com.example.group03_voicerecorder_mobile.app;

import android.app.PendingIntent;
import android.os.Build;

public class GlobalConstants {
    private GlobalConstants() {}
    public static final int PENDING_INTENT_FLAGS;
    static {
        PENDING_INTENT_FLAGS = PendingIntent.FLAG_IMMUTABLE;
    }

    public static final String APPLICATION_NAME = "AudIOWave";
    public static final String STORAGE_DIR = "records";
    public static final String SHARED_PREFERENCES = "app_preferences";
    public static final String SEPARATOR = ", ";
    public static final String EXTENSION_SEPARATOR = ".";
    public static final String DEFAULT_RECORD_NAME = "Record -";
    public static final String THEME_DEFAULT = "Default";
    public static final String THEME_TEAL = "Teal";
    public static final String THEME_BLUE = "Blue";
    public static final String THEME_PURPLE = "Purple";
    public static final String THEME_PINK = "Pink";
    public static final String THEME_ORANGE = "Orange";
    public static final String THEME_RED = "Red";
    public static final String[] FORMATS_SUPPORTED = {"m4a", "wav", "mp3", "3gpp", "3gp", "amr", "aac", "flac", "ogg", "mp4"};
    public static final String[] THEMES = {"Default", "Blue", "Orange", "Pink", "Purple", "Red", "Teal"};
    public static final String FORMAT_MP4 = ".mp4";
    public static final String FORMAT_WAV = ".wav";
    public static final String FORMAT_MP3 = ".mp3";
    public static final String FORMAT_3GPP = ".3gpp";
    public static final String FORMAT_3GP = ".3gp";
    public static final String FORMAT_AMR = ".amr";
    public static final String FORMAT_AAC = ".aac";
    public static final String FORMAT_FLAC = ".flac";
    public static final String FORMAT_OGG = ".ogg";
    public static final String FORMAT_M4A = ".m4a";
}

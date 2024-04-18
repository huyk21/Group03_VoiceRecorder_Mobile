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
    public static final String SHARED_PREFERENCES = "user_settings";
    public static final String SEPARATOR = ", ";
    public static final String EXTENSION_SEPARATOR = ".";
    public static final String DEFAULT_RECORD_NAME = "Record -";
    public static final String THEME_BLACK = "black";
    public static final String THEME_TEAL = "teal";
    public static final String THEME_BLUE = "blue";
    public static final String THEME_PURPLE = "purple";
    public static final String THEME_PINK = "pink";
    public static final String THEME_ORANGE = "orange";
    public static final String THEME_RED = "red";
    public static final String THEME_BROWN = "brown";
    public static final String THEME_BLUE_GREY = "blue_gray";

    public static final String[] FORMATS_SUPPORTED = {"mp4", "wav", "mp3", "3gpp", "3gp", "amr", "aac", "flac", "ogg", "m4a"};

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

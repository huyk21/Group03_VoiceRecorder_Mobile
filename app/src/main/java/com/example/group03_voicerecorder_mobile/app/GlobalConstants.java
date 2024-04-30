package com.example.group03_voicerecorder_mobile.app;

import android.app.PendingIntent;
import android.media.MediaRecorder;
import android.os.Build;

import java.util.HashMap;

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
    public static final String[] FORMATS_SUPPORTED = {"m4a", "mp3", "3gpp", "3gp", "amr", "aac", "flac", "ogg", "mp4"};
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
    public static HashMap<String, Integer> FORMAT_MAP = new HashMap<>();
    static {
        FORMAT_MAP.put("MP4", MediaRecorder.OutputFormat.MPEG_4);
        FORMAT_MAP.put("3GP", MediaRecorder.OutputFormat.THREE_GPP);
        FORMAT_MAP.put("AMR", MediaRecorder.OutputFormat.AMR_NB);
        // Add other formats as needed
    }
    public static HashMap<String, Integer> ENCODER_MAP = new HashMap<>();
    static {
        ENCODER_MAP.put("MP4", MediaRecorder.AudioEncoder.AAC);  // MPEG-4 files typically use AAC encoding
        ENCODER_MAP.put("M4A", MediaRecorder.AudioEncoder.HE_AAC);  // M4A files use High Efficiency AAC
        ENCODER_MAP.put("MP3", MediaRecorder.AudioEncoder.AAC);  // Android doesn't support MP3 encoding natively; AAC is often used instead for MP3 file containers
        ENCODER_MAP.put("3GPP", MediaRecorder.AudioEncoder.AMR_NB);  // 3GPP files use AMR-NB
        ENCODER_MAP.put("AMR", MediaRecorder.AudioEncoder.AMR_NB);  // AMR files use AMR-NB
        ENCODER_MAP.put("AAC", MediaRecorder.AudioEncoder.AAC);  // Raw AAC encoding
        ENCODER_MAP.put("FLAC", MediaRecorder.AudioEncoder.DEFAULT);  // Android does not support FLAC in MediaRecorder; might use external library
        ENCODER_MAP.put("OGG", MediaRecorder.AudioEncoder.VORBIS);  // OGG files typically use Vorbis

    }

}


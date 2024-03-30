package com.example.group03_voicerecorder_mobile.exception;

public abstract class ApplicationException extends Exception{
    public abstract int getExcepType();

    public static final int FILE_CREATE_EXCEPTION = 0;
    public static final int INVALID_OUTPUT_EXCEPTION = 1;
    public static final int RECORDING_EXCEPTION = 2;
    public static final int RECORDER_EXCEPTION = 3;
    public static final int PERMISSION_READ_EXCEPTION = 4;
}

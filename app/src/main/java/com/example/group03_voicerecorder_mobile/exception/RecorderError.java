package com.example.group03_voicerecorder_mobile.exception;

public class RecorderError extends ApplicationException{
    @Override
    public int getExcepType() {
        return ApplicationException.RECORDER_EXCEPTION;
    }
}

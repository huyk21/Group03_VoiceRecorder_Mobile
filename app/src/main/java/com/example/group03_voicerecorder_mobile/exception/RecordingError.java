package com.example.group03_voicerecorder_mobile.exception;

public class RecordingError extends ApplicationException{
    @Override
    public int getExcepType() {
        return ApplicationException.RECORDING_EXCEPTION;
    }
}

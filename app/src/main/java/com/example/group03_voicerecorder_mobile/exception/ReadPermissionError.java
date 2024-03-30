package com.example.group03_voicerecorder_mobile.exception;

public class ReadPermissionError extends ApplicationException{
    @Override
    public int getExcepType() {
        return ApplicationException.PERMISSION_READ_EXCEPTION;
    }
}

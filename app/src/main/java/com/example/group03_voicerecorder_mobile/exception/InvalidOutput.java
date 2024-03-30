package com.example.group03_voicerecorder_mobile.exception;

public class InvalidOutput extends ApplicationException{
    @Override
    public int getExcepType() {
        return ApplicationException.INVALID_OUTPUT_EXCEPTION;
    }
}

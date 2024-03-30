package com.example.group03_voicerecorder_mobile.exception;

public class FileCreate extends ApplicationException{
    @Override
    public int getExcepType() {
        return ApplicationException.FILE_CREATE_EXCEPTION;
    }
}


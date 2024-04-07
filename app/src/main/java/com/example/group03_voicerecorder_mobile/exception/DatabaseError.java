package com.example.group03_voicerecorder_mobile.exception;

public class DatabaseError extends ApplicationException{
    @Override
    public int getExcepType() {
        return ApplicationException.DATABASE_WRITE_EXCEPTION;
    }
}

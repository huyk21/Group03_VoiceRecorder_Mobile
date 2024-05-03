package com.example.group03_voicerecorder_mobile.audio;

public interface TrimAudioCallback {
    void onSuccess(int resultDuration, String downloadUrl);
    void onFailure(String errorMessage);
}

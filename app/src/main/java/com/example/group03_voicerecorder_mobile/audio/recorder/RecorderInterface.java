package com.example.group03_voicerecorder_mobile.audio.recorder;

import com.example.group03_voicerecorder_mobile.exception.ApplicationException;

import java.io.File;

public interface RecorderInterface {
    interface RecorderCallback {
        void onStart(File outputFile);
        void onPause();
        void onResume();
        void onRecording();
        void onStop(File outputFile);
        void onError(ApplicationException exc);
    }

    interface Recorder {
        void setCallBack(RecorderCallback callBack);
        void startRecord(String output, int channel, int bitrate, int sampleRate);
        void pauseRecord();
        void resumeRecord();
        void stopRecord();
        boolean isRecording();
    }
}

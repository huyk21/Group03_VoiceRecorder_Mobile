package com.example.group03_voicerecorder_mobile.audio.recorder;

public class AudioRecorder implements RecorderInterface.Recorder{
    @Override
    public void setCallBack(RecorderInterface.RecorderCallback callBack) {
        System.out.println("adu");
    }

    @Override
    public void startRecord(String output, int channel, int bitrate, int sampleRate) {

    }

    @Override
    public void pauseRecord() {

    }

    @Override
    public void resumeRecord() {

    }

    @Override
    public void stopRecord() {

    }

    @Override
    public boolean isRecording() {
        return false;
    }
}

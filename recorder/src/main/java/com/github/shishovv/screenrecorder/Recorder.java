package com.github.shishovv.screenrecorder;

import org.jetbrains.annotations.NotNull;

public interface Recorder {

    void startRecording();
    void stopRecording();
    void stopRecordingAndSave(@NotNull final String outFilePath);
    boolean isStopped();
}

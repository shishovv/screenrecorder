package com.github.shishovv.screenrecorder.video;

public enum VideoCodec {
    H264("mov"), TSCC("avi");

    public final String videoFormat;

    VideoCodec(String videoFormat) {
        this.videoFormat = videoFormat;
    }
}

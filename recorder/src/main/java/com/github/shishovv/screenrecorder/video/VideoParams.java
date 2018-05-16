package com.github.shishovv.screenrecorder.video;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class VideoParams {

    public final VideoCodec videoCodec;
    public final Rectangle captureArea;
    public final int frameRate;

    private VideoParams(@NotNull VideoCodec videoCodec, @NotNull final Rectangle captureArea, int frameRate) {
        this.videoCodec = videoCodec;
        this.captureArea = captureArea;
        this.frameRate = frameRate;
    }

    @NotNull
    public static VideoParams newParams(@NotNull VideoCodec videoCodec, int width, int height, int frameRate) {
        return new VideoParams(videoCodec, new Rectangle(width, height), frameRate);
    }

    @NotNull
    public static VideoParams newParams(@NotNull VideoCodec videoCodec, @NotNull final Rectangle captureArea, int frameRate) {
        return new VideoParams(videoCodec, captureArea, frameRate);
    }
}

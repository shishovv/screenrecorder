package com.github.shishovv.screenrecorder.video;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class VideoParams {

    public final Rectangle captureArea;
    public final int frameRate;

    private VideoParams(@NotNull final Rectangle captureArea, int frameRate) {
        this.captureArea = captureArea;
        this.frameRate = frameRate;
    }

    @NotNull
    public static VideoParams newParams(int width, int height, int frameRate) {
        return new VideoParams(new Rectangle(width, height), frameRate);
    }

    @NotNull
    public static VideoParams newParams(@NotNull final Rectangle captureArea, int frameRate) {
        return new VideoParams(captureArea, frameRate);
    }
}

package com.github.shishovv.screenrecorder.video;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class VideoParams {

    public static final VideoParams DEFAULT_PARAMS =
            new VideoParams(VideoCodec.TSCC, new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()), 15);

    public final VideoCodec videoCodec;
    public final Rectangle captureArea;
    public final int frameRate;

    private VideoParams(@NotNull VideoCodec videoCodec, @NotNull final Rectangle captureArea, int frameRate) {
        this.videoCodec = videoCodec;
        this.captureArea = captureArea;
        this.frameRate = frameRate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private VideoCodec videoCodec;
        private Rectangle captureArea;
        private int frameRate;

        public Builder() {
            videoCodec = DEFAULT_PARAMS.videoCodec;
            frameRate = DEFAULT_PARAMS.frameRate;
            captureArea = DEFAULT_PARAMS.captureArea;
        }

        public Builder setVideoCodec(@NotNull final VideoCodec videoCodec) {
            this.videoCodec = videoCodec;
            return this;
        }

        public Builder setCaptureArea(@NotNull final Rectangle captureArea) {
            this.captureArea = captureArea;
            return this;
        }

        public Builder setFrameRate(final int frameRate) {
            this.frameRate = frameRate;
            return this;
        }

        public VideoParams build() {
            return new VideoParams(videoCodec, captureArea, frameRate);
        }
    }
}

package com.github.shishovv.screenrecorder.video;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Logger;

class VideoMaker {

    private static final Logger LOG = Logger.getLogger(VideoMaker.class.getSimpleName());

    @NotNull
    private final VideoParams params;

    private VideoMaker(@NotNull final VideoParams params) {
        this.params = params;
    }

    static VideoMaker newInstance(@NotNull final VideoParams params) {
        return new VideoMaker(params);
    }

    void makeVideoAndSave(@NotNull Iterable<BufferedImage> images,
                          @NotNull String outFile) {
        LOG.info("start making video...");
        try {
            AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(new File(outFile), params.frameRate);
            for (final BufferedImage image : images) {
                encoder.encodeImage(image);
            }
            encoder.finish();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
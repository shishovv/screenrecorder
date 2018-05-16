package com.github.shishovv.screenrecorder.video;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.logging.Logger;

class H264VideoMaker implements VideoMaker {

    private static final Logger LOG = Logger.getLogger(H264VideoMaker.class.getSimpleName());

    @NotNull
    private final VideoParams params;

    private H264VideoMaker(@NotNull final VideoParams params) {
        this.params = params;
    }

    static H264VideoMaker newInstance(@NotNull final VideoParams params) {
        return new H264VideoMaker(params);
    }

    @Override
    public void makeVideoAndSave(@NotNull Iterable<BufferedImage> images,
                                 @NotNull Path outPath) {
        LOG.info("start making video...");
        try {
            AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(outPath.toFile(), params.frameRate);
            for (final BufferedImage image : images) {
                encoder.encodeImage(image);
            }
            encoder.finish();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

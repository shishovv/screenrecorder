package com.github.shishovv.screenrecorder.video;

import com.github.shishovv.screenrecorder.util.FileUtils;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            final Path outPath = Paths.get(outFile);
            FileUtils.createDirsIfNotExists(outPath);
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

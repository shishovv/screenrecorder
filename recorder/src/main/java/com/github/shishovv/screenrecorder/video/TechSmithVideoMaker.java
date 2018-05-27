package com.github.shishovv.screenrecorder.video;

import org.jetbrains.annotations.NotNull;
import org.monte.media.Buffer;
import org.monte.media.Codec;
import org.monte.media.Format;
import org.monte.media.MovieWriter;
import org.monte.media.avi.AVIWriter;
import org.monte.media.avi.TechSmithCodec;
import org.monte.media.math.Rational;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.VideoFormatKeys.*;
import static org.monte.media.VideoFormatKeys.MIME_AVI;
import static org.monte.media.VideoFormatKeys.MediaType;
import static org.monte.media.VideoFormatKeys.MimeTypeKey;

class TechSmithVideoMaker implements VideoMaker {

    private static final Logger LOG = Logger.getLogger(TechSmithVideoMaker.class.getSimpleName());

    private static final Format SCREEN_FORMAT = new Format(
            MediaTypeKey, MediaType.VIDEO,
            EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
            DepthKey, 24);
    private static final Format IMAGE_FORMAT = new Format(
            MediaTypeKey, MediaType.VIDEO,
            EncodingKey, ENCODING_BUFFERED_IMAGE);

    @NotNull
    private final VideoParams params;

    private TechSmithVideoMaker(@NotNull final VideoParams params) {
        this.params = params;
    }

    public static TechSmithVideoMaker newInstance(@NotNull final VideoParams params) {
        return new TechSmithVideoMaker(params);
    }

    @Override
    public void makeVideoAndSave(@NotNull Iterable<BufferedImage> images,
                                 @NotNull Path outPath) {
        LOG.info("start making video...");

        MovieWriter movieWriter = null;
        try {
            movieWriter = new AVIWriter(outPath.toFile());
            final int videoTrack = movieWriter.addTrack(getOutputFormat());
            final Codec encoder = createEncoder();

            final Buffer imgBuffer = new Buffer();
            imgBuffer.format = IMAGE_FORMAT;
            imgBuffer.track = videoTrack;
            final Buffer compressedImgBuffer = new Buffer();
            for (final BufferedImage image : images) {
                imgBuffer.data = image;
                tryEncode(encoder, imgBuffer, compressedImgBuffer);
                movieWriter.write(videoTrack, compressedImgBuffer);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            if (movieWriter != null) {
                try {
                    movieWriter.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @NotNull
    private Codec createEncoder() {
        final Codec encoder = new TechSmithCodec();
        encoder.setInputFormat(getInputFormat());
        encoder.setOutputFormat(getOutputFormat());
        return encoder;
    }

    private void tryEncode(@NotNull final Codec encoder, @NotNull final Buffer in, @NotNull final Buffer out) {
        if (encoder.process(in, out) != Codec.CODEC_OK) {
            throw new RuntimeException("encode failed");
        }
    }

    @NotNull
    private Format getInputFormat() {
        return SCREEN_FORMAT.prepend(
                EncodingKey, ENCODING_BUFFERED_IMAGE,
                FrameRateKey, Rational.valueOf(params.frameRate),
                WidthKey,  params.captureArea.width,
                HeightKey, params.captureArea.height);
    }

    @NotNull
    private Format getOutputFormat() {
        return SCREEN_FORMAT.prepend(
                MimeTypeKey, MIME_AVI,
                FrameRateKey, Rational.valueOf(params.frameRate),
                WidthKey, params.captureArea.width,
                HeightKey, params.captureArea.height);
    }
}

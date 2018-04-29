package screenrecorder.video;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
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
        SeekableByteChannel out = null;
        try {
            out = NIOUtils.writableFileChannel(outFile);
            AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(params.frameRate, 1));
            for (final BufferedImage image : images) {
                encoder.encodeImage(image);
            }
            encoder.finish();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            NIOUtils.closeQuietly(out);
        }
    }
}

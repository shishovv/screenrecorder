package screenrecorder.video;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jetbrains.annotations.NotNull;
import screenrecorder.util.Log;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Logger;

public class VideoMaker {

    @NotNull
    private final VideoParams params;

    private VideoMaker(@NotNull final VideoParams params) {
        this.params = params;
    }

    public static VideoMaker newInstance(@NotNull final VideoParams params) {
        return new VideoMaker(params);
    }

    public void makeVideoAndSave(@NotNull Iterable<BufferedImage> images,
                                 @NotNull String outFile) {
        Log.i("start making video...");
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

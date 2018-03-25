package screenrecorder.video;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;

class ImageStream implements PullBufferStream {

    @NotNull
    private final VideoFormat format;
    @NotNull
    private final Iterator<BufferedImage> images;

    private ImageStream(@NotNull final VideoFormat format, @NotNull final Iterator<BufferedImage> images) {
        this.format = format;
        this.images = images;
    }

    @NotNull
    public static PullBufferStream newStream(@NotNull final VideoParams params, @NotNull final Iterable<BufferedImage> images) {
        return new ImageStream(
                new VideoFormat(VideoFormat.JPEG,
                        new Dimension(params.width, params.height),
                        Format.NOT_SPECIFIED,
                        Format.byteArray,
                        (float) params.frameRate),
                images.iterator());
    }

    @Override
    public boolean willReadBlock() {
        return false;
    }

    @Override
    public void read(Buffer buffer) {
        if (!images.hasNext()) {
            buffer.setEOM(true);
            buffer.setOffset(0);
            buffer.setLength(0);
            return;
        }

        final byte[] data = toByteArray(images.next());
        buffer.setData(data);
        buffer.setOffset(0);
        buffer.setLength(data.length);
        buffer.setFormat(format);
        buffer.setFlags(buffer.getFlags() | Buffer.FLAG_KEY_FRAME);
    }

    private byte[] toByteArray(final BufferedImage image) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Format getFormat() {
        return format;
    }

    @Override
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    @Override
    public long getContentLength() {
        return 0;
    }

    @Override
    public boolean endOfStream() {
        return !images.hasNext();
    }

    @Override
    public Object[] getControls() {
        return new Object[0];
    }

    @Override
    public Object getControl(String s) {
        return null;
    }
}

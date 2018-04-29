package screenrecorder.video.jmf;

import org.jetbrains.annotations.NotNull;
import screenrecorder.video.VideoParams;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

class ImageStream implements PullBufferStream {

    @NotNull
    private final VideoFormat format;
    @NotNull
    private final Iterator<Path> images;

    private ImageStream(@NotNull final VideoFormat format, @NotNull final Iterator<Path> images) {
        this.format = format;
        this.images = images;
    }

    @NotNull
    public static PullBufferStream newStream(@NotNull final VideoParams params, @NotNull final Iterable<Path> images) {
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
    public void read(Buffer buffer) throws IOException {
        if (!images.hasNext()) {
            buffer.setEOM(true);
            buffer.setOffset(0);
            buffer.setLength(0);
            return;
        }

        final byte[] data = Files.readAllBytes(images.next());
        buffer.setData(data);
        buffer.setOffset(0);
        buffer.setLength(data.length);
        buffer.setFormat(format);
        buffer.setFlags(buffer.getFlags() | Buffer.FLAG_KEY_FRAME);
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

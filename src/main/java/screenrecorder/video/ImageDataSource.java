package screenrecorder.video;

import org.jetbrains.annotations.NotNull;

import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

class ImageDataSource extends PullBufferDataSource {

    @NotNull
    private final PullBufferStream[] streams;

    private ImageDataSource(@NotNull final PullBufferStream[] streams) {
        this.streams = streams;
    }

    @NotNull
    public static ImageDataSource newDataDource(@NotNull final VideoParams params, @NotNull final Iterable<BufferedImage> images) {
        final PullBufferStream[] streams = new PullBufferStream[1];
        streams[0] = ImageStream.newStream(params, images);
        return new ImageDataSource(streams);
    }

    @Override
    public PullBufferStream[] getStreams() {
        return streams;
    }

    @Override
    public String getContentType() {
        return ContentDescriptor.RAW;
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Object getControl(String s) {
        return null;
    }

    @Override
    public Object[] getControls() {
        return new Object[0];
    }

    @Override
    public Time getDuration() {
        return DURATION_UNKNOWN;
    }
}

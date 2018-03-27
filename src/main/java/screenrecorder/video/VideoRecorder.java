package screenrecorder.video;

import org.jetbrains.annotations.NotNull;
import screenrecorder.Recorder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VideoRecorder implements Recorder {

    private static final int DEFAULT_FRAMERATE = 5;

    @NotNull
    private final VideoParams params;
    private CompletableFuture<List<BufferedImage>> futureImages;

    private volatile boolean stopped;

    private VideoRecorder(@NotNull final VideoParams params) {
        this.params = params;
    }

    @NotNull
    public static Recorder newRecorder() {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        return new VideoRecorder(VideoParams.newParams(dimension.width, dimension.height, DEFAULT_FRAMERATE));
    }

    @NotNull
    public static Recorder newRecorder(@NotNull final VideoParams params) {
        return new VideoRecorder(params);
    }

    @Override
    public void startRecording() {
        futureImages = CompletableFuture.supplyAsync(() -> {
            final ScreenShotter screenShotter =
                    ScreenShotter.newInstance(new Rectangle(new Dimension(params.width, params.height)));
            final List<BufferedImage> images = new ArrayList<>(100);
            while (!stopped) {
                images.add(screenShotter.takeScreenshot());
            }
            return images;
        });
    }

    @Override
    public void stopRecording() {
        if (futureImages == null) {
            throw new IllegalStateException("not started");
        }
        stopped = true;
    }

    @Override
    public void stopRecordingAndSave(@NotNull final String outFilePath) {
        stopRecording();
        try {
            VideoMaker.newVideoMaker(outFilePath).makeVideo(params, futureImages.get());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

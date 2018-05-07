package screenrecorder.video;

import org.jetbrains.annotations.NotNull;
import screenrecorder.Recorder;
import screenrecorder.util.ImageUtils;
import screenrecorder.util.Screenshotter;

import java.awt.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ScreenRecorder implements Recorder {

    private static final Logger LOG = Logger.getLogger(ScreenRecorder.class.getSimpleName());

    private static final int DEFAULT_FRAMERATE = 5;
    private static final int TERMINATION_TIMEOUT_IN_SECONDS = 10;

    @NotNull
    private final VideoParams params;
    @NotNull
    private final CircularImageBuffer imagesStorage;
    @NotNull
    private final ExecutorService executor;

    private volatile boolean stopped;

    private ScreenRecorder(@NotNull final VideoParams params, final int videoDuration, @NotNull final TimeUnit timeUnit) {
        this.params = params;
        this.imagesStorage = CircularImageBuffer.newBuffer((int) timeUnit.toSeconds(videoDuration) * params.frameRate);
        executor = Executors.newSingleThreadExecutor();
    }

    @NotNull
    public static Recorder newRecorder(final int videoDuration, @NotNull final TimeUnit timeUnit) {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        return new ScreenRecorder(VideoParams.newParams(dimension.width, dimension.height, DEFAULT_FRAMERATE), videoDuration, timeUnit);
    }

    @NotNull
    public static Recorder newRecorder(@NotNull final VideoParams params, final int videoDuration, @NotNull final TimeUnit timeUnit) {
        return new ScreenRecorder(params, videoDuration, timeUnit);
    }

    @Override
    public void startRecording() {
        if (executor.isShutdown()) {
            throw new IllegalStateException();
        }
        LOG.info("start recording...");
        executor.execute(() -> {
            final Screenshotter screenshotter =
                    Screenshotter.newInstance(params.captureArea);
            while (!stopped) {
                imagesStorage.putAsync(screenshotter.takeScreenshot());
            }
        });
    }

    private void stop() {
        LOG.info("stop recording...");
        stopped = true;
        executor.shutdown();
        try {
            executor.awaitTermination(TERMINATION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopRecording() {
        try {
            stop();
        } finally {
            imagesStorage.deleteImages();
        }
    }

    @Override
    public void stopRecordingAndSave(@NotNull final String outFilePath) {
        try {
            stop();
            VideoMaker.newInstance(params).makeVideoAndSave(ImageUtils.drawCursors(imagesStorage), outFilePath);
        } finally {
            imagesStorage.deleteImages();
        }
    }
}

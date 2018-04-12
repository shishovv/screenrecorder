package screenrecorder.video;

import org.jetbrains.annotations.NotNull;
import screenrecorder.Recorder;
import screenrecorder.util.CircularImageBuffer;
import screenrecorder.util.ImageUtils;
import screenrecorder.util.Log;
import screenrecorder.util.ScreenShotter;

import java.awt.*;
import java.util.concurrent.*;

public class VideoRecorder implements Recorder {

    private static final int DEFAULT_FRAMERATE = 6;
    private static final int TERMINATION_TIMEOUT_IN_SECONDS = 10;

    @NotNull
    private final VideoParams params;
    @NotNull
    private final CircularImageBuffer imagesStorage;
    @NotNull
    private final ExecutorService executor;

    private volatile boolean stopped;

    private VideoRecorder(@NotNull final VideoParams params, final long sizeLimit) {
        this.params = params;
        this.imagesStorage = CircularImageBuffer.newBuffer(sizeLimit);
        executor = Executors.newSingleThreadExecutor();
    }

    @NotNull
    public static Recorder newRecorder(final int sizeLimit) {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        return new VideoRecorder(VideoParams.newParams(dimension.width, dimension.height, DEFAULT_FRAMERATE), sizeLimit);
    }

    @NotNull
    public static Recorder newRecorder(@NotNull final VideoParams params, final int sizeLimit) {
        return new VideoRecorder(params, sizeLimit);
    }

    @Override
    public void startRecording() {
        if (executor.isShutdown()) {
            throw new IllegalStateException();
        }
        Log.i("start recording...");
        executor.execute(() -> {
            final ScreenShotter screenShotter =
                    ScreenShotter.newInstance(new Rectangle(new Dimension(params.width, params.height)));
            while (!stopped) {
                imagesStorage.putAsync(screenShotter.takeScreenshot());
            }
        });
    }

    private void stop() {
        Log.i("stop recording...");
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

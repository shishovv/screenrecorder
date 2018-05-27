package com.github.shishovv.screenrecorder.video;

import com.github.shishovv.screenrecorder.Recorder;
import com.github.shishovv.screenrecorder.util.FileUtils;
import com.github.shishovv.screenrecorder.util.Screenshotter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static com.github.shishovv.screenrecorder.util.Require.require;
import static com.github.shishovv.screenrecorder.util.Require.requireNotNull;

public class ScreenRecorder implements Recorder {

    private static final Logger LOG = Logger.getLogger(ScreenRecorder.class.getSimpleName());

    private static final int DEFAULT_FRAMERATE = 30;
    private static final int TERMINATION_TIMEOUT_IN_SECONDS = 10;
    private static final int CURSOR_CAPTURE_COUNT = 4;

    @NotNull
    private final VideoParams params;
    @NotNull
    private final ExecutorService executor;
    private Future<CircularImageBuffer> futureImages;
    private final int videoDurationInSec;
    private volatile boolean stopped;

    private ScreenRecorder(@NotNull final VideoParams params, final int videoDuration, @NotNull final TimeUnit timeUnit) {
        this.params = params;
        videoDurationInSec = (int) timeUnit.toSeconds(videoDuration);
        executor = Executors.newSingleThreadExecutor();
    }

    @NotNull
    public static Recorder newRecorder(final int videoDuration, @NotNull final TimeUnit timeUnit) {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        return new ScreenRecorder(
                VideoParams.newParams(VideoCodec.TSCC, dimension.width, dimension.height, DEFAULT_FRAMERATE),
                videoDuration,
                timeUnit);
    }

    @NotNull
    public static Recorder newRecorder(@NotNull final VideoParams params, final int videoDuration, @NotNull final TimeUnit timeUnit) {
        return new ScreenRecorder(params, videoDuration, timeUnit);
    }

    @Override
    public void startRecording() {
        require(!executor.isShutdown(), "cannot perform this action after stop");
        require(futureImages == null, "cannot perform this after start");

        LOG.info("start recording...");
        futureImages = executor.submit(() -> {
            final CircularImageBuffer buffer =
                    CircularImageBuffer.newBuffer(videoDurationInSec * params.frameRate / CURSOR_CAPTURE_COUNT);
            final Screenshotter screenshotter =
                    Screenshotter.newInstance(params.captureArea);
            while (!stopped) {
                buffer.putAsync(screenshotter.takeScreenshot());
            }
            return buffer;
        });
    }

    @NotNull
    private CircularImageBuffer stopAndGetImages() {
        requireNotNull(futureImages, "record is not started");
        LOG.info("stop recording...");

        stopped = true;
        executor.shutdown();
        try {
            executor.awaitTermination(TERMINATION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            return futureImages.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopRecording() {
        stopAndGetImages().deleteImages();
    }

    @Override
    public void stopRecordingAndSave(@NotNull final String outFilePath) {
        CircularImageBuffer images = null;
        try {
            images = stopAndGetImages();
            final Path outPath = Paths.get(outFilePath);
            FileUtils.createDirsIfNotExists(outPath);
            getVideoMaker().makeVideoAndSave(images, outPath);
        } finally {
            if (images != null) {
                images.deleteImages();
            }
        }
    }

    @NotNull
    private VideoMaker getVideoMaker() {
        switch (params.videoCodec) {
            case H264:
                return H264VideoMaker.newInstance(params);
            case TSCC:
                return TechSmithVideoMaker.newInstance(params);
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }
}

package com.github.shishovv.screenrecorder;

import com.github.shishovv.screenrecorder.video.ScreenRecorder;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class ScreenRecorderRule extends TestWatcher {

    private final Recorder recorder;
    private final String outDir;

    public ScreenRecorderRule(final int duration, final TimeUnit timeUnit, final String outDir) {
        recorder = ScreenRecorder.newRecorder(duration, timeUnit);
        this.outDir = outDir;
    }

    @Override
    protected void starting(Description description) {
        recorder.startRecording();
    }

    @Override
    protected void failed(Throwable e, Description description) {
        final String fileName = description.getMethodName()
                + "_"
                + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                + ".mov";
        final String outFile = Paths.get(outDir, fileName).toString();
        recorder.stopRecordingAndSave(outFile);
    }

    @Override
    protected void finished(Description description) {
        if (!recorder.isStopped()) {
            recorder.stopRecording();
        }
    }
}

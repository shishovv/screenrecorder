package screenrecorder;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import screenrecorder.Recorder;
import screenrecorder.video.ScreenRecorder;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class VideoRecorderRule extends TestWatcher {

    private final Recorder recorder;
    private final String outDir;

    public VideoRecorderRule(final int duration, final TimeUnit timeUnit, final String outDir) {
        recorder = ScreenRecorder.newRecorder(duration, timeUnit);
        this.outDir = outDir;
    }

    @Override
    protected void starting(Description description) {
        recorder.startRecording();
    }

    @Override
    protected void failed(Throwable e, Description description) {
        final String outFile = Paths.get(outDir, description.getMethodName() + ".mov").toString();
        recorder.stopRecordingAndSave(outFile);
    }

    @Override
    protected void finished(Description description) {
        recorder.stopRecording();
    }
}

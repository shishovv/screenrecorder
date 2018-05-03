import screenrecorder.Recorder;
import screenrecorder.video.ScreenRecorder;

import java.util.concurrent.TimeUnit;

public class Sample {

    public static void main(String[] args) throws Exception {
        final Recorder recorder = ScreenRecorder.newRecorder(10, TimeUnit.SECONDS);
        recorder.startRecording();
        Thread.sleep(10_000);
        recorder.stopRecordingAndSave("sample.mov");
    }
}

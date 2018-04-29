import screenrecorder.Recorder;
import screenrecorder.video.ScreenRecorder;

public class Sample {

    public static void main(String[] args) throws Exception {
        final Recorder recorder = ScreenRecorder.newRecorder(50 * 1024 * 1024);
        recorder.startRecording();
        Thread.sleep(30_000);
        recorder.stopRecordingAndSave("sample.mov");
    }
}

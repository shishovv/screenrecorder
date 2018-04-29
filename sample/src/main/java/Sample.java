import screenrecorder.Recorder;
import screenrecorder.video.VideoRecorder;

public class Sample {

    public static void main(String[] args) throws Exception {
        final Recorder recorder = VideoRecorder.newRecorder(50 * 1024 * 1024);
        recorder.startRecording();
        Thread.sleep(30_000);
        recorder.stopRecordingAndSave("sample.mov");
    }
}

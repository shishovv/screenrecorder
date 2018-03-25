package screenrecorder;

import org.jetbrains.annotations.NotNull;
import screenrecorder.video.VideoParams;
import screenrecorder.video.VideoRecorder;

public class Main {

    public static void main(String[] args) throws Exception {
        final Recorder recorder = VideoRecorder.newRecorder();
        recorder.startRecording();
        Thread.sleep(15 * 1000);
        recorder.stopRecordingAndSave("/home/vladislav/Documents/null/test/ok.mov");
    }

    private static VideoParams parseArgs(@NotNull final String[] args) {
        return VideoParams.newParams(parseInt(args[0]), parseInt(args[1]), parseInt(args[2]));
    }

    private static int parseInt(final String s) {
        return Integer.parseInt(s);
    }
}

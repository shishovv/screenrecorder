import screenrecorder.Recorder;
import screenrecorder.video.ScreenRecorder;
import screenrecorder.video.VideoParams;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Sample {

    public static void main(String[] args) throws Exception {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        final Recorder recorder =
                ScreenRecorder.newRecorder(VideoParams.newParams(dimension.width, dimension.height, 8), 10, TimeUnit.SECONDS);
        recorder.startRecording();
        Thread.sleep(10_000);
        recorder.stopRecordingAndSave("sample.mov");
    }
}

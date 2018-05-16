import com.github.shishovv.screenrecorder.Recorder;
import com.github.shishovv.screenrecorder.video.ScreenRecorder;
import com.github.shishovv.screenrecorder.video.VideoCodec;
import com.github.shishovv.screenrecorder.video.VideoParams;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Sample {

    public static void main(String[] args) throws Exception {
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        final Recorder recorder =
                ScreenRecorder.newRecorder(VideoParams.newParams(VideoCodec.TSCC, dimension.width, dimension.height, 30), 30, TimeUnit.SECONDS);
        recorder.startRecording();
        Thread.sleep(30_000);
        recorder.stopRecordingAndSave("sample.avi");
    }
}

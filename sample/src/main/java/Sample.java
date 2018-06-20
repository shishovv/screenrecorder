import com.github.shishovv.screenrecorder.Recorder;
import com.github.shishovv.screenrecorder.video.ScreenRecorder;
import com.github.shishovv.screenrecorder.video.VideoCodec;
import com.github.shishovv.screenrecorder.video.VideoParams;

import java.util.concurrent.TimeUnit;

public class Sample {

    public static void main(String[] args) throws Exception {
        final Recorder recorder =
                ScreenRecorder.newRecorder(VideoParams.builder()
                                .setVideoCodec(VideoCodec.TSCC)
                                .setCaptureArea(VideoParams.DEFAULT_PARAMS.captureArea)
                                .setFrameRate(VideoParams.DEFAULT_PARAMS.frameRate)
                                .build(), ScreenRecorder.MAX_VIDEO_DURATION_IN_SECONDS, TimeUnit.SECONDS);
        recorder.startRecording();
        Thread.sleep(30_000);
        recorder.stopRecordingAndSave("sample.avi");
    }
}

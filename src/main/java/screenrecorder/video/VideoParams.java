package screenrecorder.video;

import org.jetbrains.annotations.NotNull;

public class VideoParams {

    public final int width;
    public final int height;
    public final int frameRate;

    private VideoParams(int width, int height, int frameRate) {
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
    }

    @NotNull
    public static VideoParams newParams(int width, int height, int frameRate) {
        return new VideoParams(width, height, frameRate);
    }
}

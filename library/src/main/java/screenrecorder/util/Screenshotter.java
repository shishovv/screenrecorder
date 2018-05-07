package screenrecorder.util;

import org.jetbrains.annotations.NotNull;
import screenrecorder.image.ImageWithCursor;

import java.awt.*;

public class Screenshotter {

    @NotNull
    private final Robot robot;
    @NotNull
    private final Rectangle rectangle;

    private Screenshotter(@NotNull final Robot robot, @NotNull final Rectangle rectangle) {
        this.robot = robot;
        this.rectangle = rectangle;
    }

    @NotNull
    public static Screenshotter newInstance(@NotNull final Rectangle rectangle) {
        try {
            return new Screenshotter(new Robot(), rectangle);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public ImageWithCursor takeScreenshot() {
        return ImageWithCursor.newImage(robot.createScreenCapture(rectangle), MouseInfo.getPointerInfo().getLocation());
    }
}

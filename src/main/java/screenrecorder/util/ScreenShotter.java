package screenrecorder.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenShotter {

    @NotNull
    private final Robot robot;
    @NotNull
    private final Rectangle rectangle;

    private ScreenShotter(@NotNull final Robot robot, @NotNull final Rectangle rectangle) {
        this.robot = robot;
        this.rectangle = rectangle;
    }

    @NotNull
    public static ScreenShotter newInstance(@NotNull final Rectangle rectangle) {
        try {
            return new ScreenShotter(new Robot(), rectangle);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public BufferedImage takeScreenshot() {
        return robot.createScreenCapture(rectangle);
    }
}

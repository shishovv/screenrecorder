package screenrecorder.util;

import org.jetbrains.annotations.NotNull;
import screenrecorder.image.ImageWithCursorPositions;

import java.awt.*;
import java.awt.image.BufferedImage;

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
    public ImageWithCursorPositions takeScreenshot() {
        final Point p1 = MouseInfo.getPointerInfo().getLocation();
        final Point p2 = MouseInfo.getPointerInfo().getLocation();
        final BufferedImage img = robot.createScreenCapture(rectangle);
        final Point p3 = MouseInfo.getPointerInfo().getLocation();
        final Point p4 = MouseInfo.getPointerInfo().getLocation();
        return ImageWithCursorPositions.newImage(img, new Point[]{p1, p2, p3, p4});
    }
}

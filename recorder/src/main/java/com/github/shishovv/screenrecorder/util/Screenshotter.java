package com.github.shishovv.screenrecorder.util;

import org.jetbrains.annotations.NotNull;
import com.github.shishovv.screenrecorder.image.ImageWithCursorPositions;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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
        final List<Point> cursorCaptures = new ArrayList<>();
        cursorCaptures.add(getCursorPosition());
        final BufferedImage img = robot.createScreenCapture(rectangle);
        cursorCaptures.add(getCursorPosition());
        return ImageWithCursorPositions.newImage(img, cursorCaptures);
    }

    private Point getCursorPosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }
}

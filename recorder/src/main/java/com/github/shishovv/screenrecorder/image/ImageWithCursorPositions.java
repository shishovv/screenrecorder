package com.github.shishovv.screenrecorder.image;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageWithCursorPositions {

    public final BufferedImage img;
    public final List<Point> cursorPositions;

    private ImageWithCursorPositions(@NotNull final BufferedImage img, @NotNull final List<Point> cursorPositions) {
        this.img = img;
        this.cursorPositions = cursorPositions;
    }

    public static ImageWithCursorPositions newImage(@NotNull final BufferedImage image, @NotNull final List<Point> cursorPositions) {
        return new ImageWithCursorPositions(image, cursorPositions);
    }
}

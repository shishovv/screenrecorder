package screenrecorder.image;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageWithCursorPositions {

    public final BufferedImage img;
    public final Point[] cursorPositions;

    private ImageWithCursorPositions(@NotNull final BufferedImage img, @NotNull final Point[] cursorPositions) {
        this.img = img;
        this.cursorPositions = cursorPositions;
    }

    public static ImageWithCursorPositions newImage(@NotNull final BufferedImage image, @NotNull final Point[] cursorPositions) {
        return new ImageWithCursorPositions(image, cursorPositions);
    }
}

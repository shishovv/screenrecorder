package screenrecorder.image;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageWithCursor {

    public final BufferedImage img;
    public final Point cursorPosition;

    private ImageWithCursor(@NotNull final BufferedImage img, @NotNull final Point cursorPosition) {
        this.img = img;
        this.cursorPosition = cursorPosition;
    }

    public static ImageWithCursor newImage(@NotNull final BufferedImage image, @NotNull final Point cursorPosition) {
        return new ImageWithCursor(image, cursorPosition);
    }
}

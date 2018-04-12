package screenrecorder.image;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageWithCursor {

    public final BufferedImage img;
    public final Point mousePosition;

    private ImageWithCursor(@NotNull final BufferedImage img, @NotNull final Point mousePosition) {
        this.img = img;
        this.mousePosition = mousePosition;
    }

    public static ImageWithCursor newImage(@NotNull final BufferedImage image, @NotNull final Point mousePosition) {
        return new ImageWithCursor(image, mousePosition);
    }
}

package screenrecorder.util;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ImageUtils {

    private static final String CURSOR_RES = "images/cursor.png";

    private ImageUtils() {}

    @NotNull
    public static BufferedImage drawCursor(@NotNull final BufferedImage image, @NotNull final Point cursorPos) {
        try {
            final BufferedImage cursorImg = ImageIO.read(FileUtils.getResourcePath(ImageUtils.class, CURSOR_RES).toFile());
            image.getGraphics()
                    .drawImage(cursorImg,
                            cursorPos.x - cursorImg.getWidth() / 2,
                            cursorPos.y - cursorImg.getHeight() / 2,
                            null);
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

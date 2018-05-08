package screenrecorder.util;

import org.jetbrains.annotations.NotNull;
import screenrecorder.image.ImageWithCursor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ImageUtils {

    private static final String CURSOR_RES = "images/cursor.png";

    private ImageUtils() {}

    @NotNull
    public static BufferedImage drawCursor(@NotNull final ImageWithCursor image) {
        try {
            final BufferedImage cursorImg = ImageIO.read(FileUtils.getResourcePath(ImageUtils.class, CURSOR_RES).toFile());
            image.img.getGraphics()
                    .drawImage(cursorImg,
                            image.cursorPosition.x - cursorImg.getWidth() / 2,
                            image.cursorPosition.y - cursorImg.getHeight() / 2,
                            null);
            return image.img;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

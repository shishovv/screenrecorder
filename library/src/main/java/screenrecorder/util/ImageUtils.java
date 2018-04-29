package screenrecorder.util;

import org.jetbrains.annotations.NotNull;
import screenrecorder.image.ImageWithCursor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    private static final Color DEFAULT_CURSOR_COLOR = new Color(255, 0, 0);
    private static final int DEFAULT_CURSOR_SIZE = 8;

    private ImageUtils() {}

    public static byte[] toByteArray(final BufferedImage image) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NotNull
    public static List<BufferedImage> drawCursors(@NotNull final Iterable<ImageWithCursor> images) {
        final List<BufferedImage> imgs = new ArrayList<>();
        for (ImageWithCursor image : images) {
            imgs.add(drawCursor(image));
        }
        return imgs;
    }

    @NotNull
    private static BufferedImage drawCursor(@NotNull final ImageWithCursor image) {
        final Graphics graphics = image.img.getGraphics();
        graphics.setColor(DEFAULT_CURSOR_COLOR);
        graphics.fillRect(image.mousePosition.x, image.mousePosition.y, DEFAULT_CURSOR_SIZE, DEFAULT_CURSOR_SIZE);
        return image.img;
    }
}

package screenrecorder.util;

import org.jetbrains.annotations.NotNull;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;

import static screenrecorder.util.Require.require;

public class ImageUtils {

    private static final String CURSOR_RES = "images/cursor.png";

    private ImageUtils() {}

    @NotNull
    public static BufferedImage drawCursor(@NotNull final BufferedImage image, @NotNull final Point cursorPos) {
        final BufferedImage cursorImg = ImageUtils.getImageResource(ImageUtils.class, CURSOR_RES);
        image.getGraphics()
                .drawImage(cursorImg,
                        cursorPos.x - cursorImg.getWidth() / 2,
                        cursorPos.y - cursorImg.getHeight() / 2,
                        null);
        return image;
    }

    public static void writeCompressed(@NotNull final BufferedImage image,
                                       @NotNull final String imageFormat,
                                       final float compressionQuality,
                                       @NotNull final OutputStream out) {
        ImageWriter writer = null;
        try (final ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(out)) {
            writer = getImageWriter(imageFormat);

            final ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(compressionQuality);

            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(image, null, null), params);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            if (writer != null) {
                writer.dispose();
            }
        }
    }

    @NotNull
    private static ImageWriter getImageWriter(@NotNull final String imageFormat) {
        final Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(imageFormat);
        require(it.hasNext());
        return it.next();
    }

    public static BufferedImage getImageResource(@NotNull final Class<?> cls, @NotNull final String resourcePath) {
        try (final InputStream inputStream = cls.getResourceAsStream(resourcePath)) {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package screenrecorder.video;

import org.jetbrains.annotations.NotNull;
import screenrecorder.image.ImageWithCursor;
import screenrecorder.util.FileUtils;
import screenrecorder.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

class CircularImageBuffer implements Iterable<ImageWithCursor> {

    private static final Logger LOG = Logger.getLogger(CircularImageBuffer.class.getSimpleName());

    private static final int TERMINATION_TIMEOUT_IN_SECONDS = 10;

    @NotNull
    private final Path tmpImagesDir;
    @NotNull
    private final List<Entry> entries;
    @NotNull
    private final ExecutorService ioExecutor;
    private final long imagesSizeLimit;

    private int nextImagePathIndex;
    private long totalSize;
    private int startIndex;
    private int endIndex;
    private boolean bufferSizeCalculated;

    private CircularImageBuffer(final long sizeLimit) {
        try {
            tmpImagesDir = Files.createTempDirectory(getClass().getSimpleName());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        entries = new ArrayList<>();
        ioExecutor = Executors.newSingleThreadExecutor();
        imagesSizeLimit = sizeLimit;
        startIndex = -1;
        endIndex = -1;
    }

    @NotNull
    static CircularImageBuffer newBuffer(final long sizeLimit) {
        return new CircularImageBuffer(sizeLimit);
    }

    void putAsync(@NotNull final ImageWithCursor image) {
        if (ioExecutor.isShutdown()) {
            throw new IllegalStateException();
        }
        ioExecutor.execute(() -> {
            final byte[] bytes = ImageUtils.toByteArray(image.img);
            final Entry entry = nextEntry(bytes.length, image.mousePosition);
            try (final OutputStream out = Files.newOutputStream(entry.imagePath, StandardOpenOption.WRITE)) {
                out.write(bytes);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @NotNull
    private Entry nextEntry(final int nextImageSize, @NotNull final Point mousePos) {
        updateEntry(nextImagePathIndex, mousePos);
        final Entry next = entries.get(nextImagePathIndex);
        updateIndices(nextImageSize);
        return next;
    }

    private void updateIndices(final int imageSize) {
        if (!bufferSizeCalculated && imageSize + totalSize > imagesSizeLimit) {
            bufferSizeCalculated = true;
        }
        if (bufferSizeCalculated) {
            nextImagePathIndex = (nextImagePathIndex + 1) % entries.size();
            startIndex = (startIndex + 1) % entries.size();
            endIndex = (endIndex + 1) % entries.size();
        } else {
            totalSize += imageSize;
            ++nextImagePathIndex;
            if (startIndex == -1) {
                startIndex = 0;
            }
            ++endIndex;
        }
    }

    private void updateEntry(final int nextPathIndex, @NotNull final Point mousePos) {
        if (nextPathIndex >= entries.size()) {
            try {
                entries.add(Entry.newEntry(
                        Files.createFile(Paths.get(tmpImagesDir.toString(), String.valueOf(nextPathIndex))),
                        mousePos));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            entries.get(nextPathIndex).mousePos = mousePos;
        }
    }

    private void awaitAllTasksCompletion() {
        try {
            ioExecutor.shutdown();
            ioExecutor.awaitTermination(TERMINATION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteImages() {
        LOG.info("deleting images...");
        awaitAllTasksCompletion();
        FileUtils.deleteDir(tmpImagesDir);
    }

    @NotNull
    @Override
    public Iterator<ImageWithCursor> iterator() {
        awaitAllTasksCompletion();
        return new ImageIterator(startIndex);
    }

    private class ImageIterator implements Iterator<ImageWithCursor> {

        private int nextIndex;

        private ImageIterator(final int startIndex) {
            nextIndex = startIndex;
        }

        @Override
        public boolean hasNext() {
            return nextIndex != endIndex;
        }

        @Override
        public ImageWithCursor next() {
            final ImageWithCursor next = ImageWithCursor.newImage(getImage(nextIndex), entries.get(nextIndex).mousePos);
            nextIndex = (nextIndex + 1) % entries.size();
            return next;
        }

        private BufferedImage getImage(final int index) {
            try {
                return ImageIO.read(entries.get(index).imagePath.toFile());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static class Entry {

        @NotNull
        final Path imagePath;
        @NotNull
        Point mousePos;

        private Entry(@NotNull final Path imagePath, @NotNull final Point mousePos) {
            this.imagePath = imagePath;
            this.mousePos = mousePos;
        }

        static Entry newEntry(@NotNull final Path imagePath, @NotNull final Point mousePos) {
            return new Entry(imagePath, mousePos);
        }
    }
}

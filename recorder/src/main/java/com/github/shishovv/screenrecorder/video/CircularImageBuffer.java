package com.github.shishovv.screenrecorder.video;

import org.jetbrains.annotations.NotNull;
import com.github.shishovv.screenrecorder.image.ImageWithCursorPositions;
import com.github.shishovv.screenrecorder.util.FileUtils;
import com.github.shishovv.screenrecorder.util.ImageUtils;

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
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.github.shishovv.screenrecorder.util.Require.require;

class CircularImageBuffer implements Iterable<BufferedImage> {

    private static final Logger LOG = Logger.getLogger(CircularImageBuffer.class.getSimpleName());

    private static final String IMAGE_FORMAT = "jpg";
    private static final float COMPRESSION_QUALITY = 0.4f;
    private static final int TERMINATION_TIMEOUT_IN_SECONDS = 10;

    @NotNull
    private final Path tmpImagesDir;
    private final Entry @NotNull [] entries;
    @NotNull
    private final ExecutorService ioExecutor;

    private int firstIndex;
    private int size;

    private CircularImageBuffer(final int capacity) {
        try {
            tmpImagesDir = Files.createTempDirectory(getClass().getSimpleName());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        entries = new Entry[capacity];
        ioExecutor = Executors.newSingleThreadExecutor();
    }

    @NotNull
    static CircularImageBuffer newBuffer(final int capacity) {
        return new CircularImageBuffer(capacity);
    }

    void putAsync(@NotNull final ImageWithCursorPositions image) {
        require(!ioExecutor.isShutdown(), "cannot perform this action");

        final Entry entry = nextEntry();
        entry.cursorPositions = image.cursorPositions;
        writeAsync(image.img, entry.imagePath);
    }

    private void writeAsync(@NotNull final BufferedImage image, @NotNull final Path path) {
        ioExecutor.execute(() -> {
            FileUtils.createFileIfNotExists(path);
            try (final OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.WRITE)) {
                ImageUtils.writeCompressed(image, IMAGE_FORMAT, COMPRESSION_QUALITY, outputStream);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @NotNull
    private Entry nextEntry() {
        final int nextIndex = normalizeIndex(firstIndex + size);
        createEntryIfNeeded(nextIndex);

        if (size != 0 && nextIndex == firstIndex) {
            firstIndex = normalizeIndex(firstIndex + 1);
        }
        if (size < capacity()) {
            ++size;
        }

        return entries[nextIndex];
    }

    private int normalizeIndex(int index) {
        return index < capacity() ? index : index % capacity();
    }

    private void createEntryIfNeeded(final int index) {
        if (entries[index] == null) {
            final Path path = Paths.get(tmpImagesDir.toString(), String.valueOf(index));
            entries[index] = new Entry(path);
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

    private int capacity() {
        return entries.length;
    }

    @NotNull
    @Override
    public Iterator<BufferedImage> iterator() {
        awaitAllTasksCompletion();
        return new ImageIterator();
    }

    private class ImageIterator implements Iterator<BufferedImage> {

        private int count;
        private int nextCursorIndex;

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public BufferedImage next() {
            final int nextIndex = normalizeIndex(firstIndex + count);
            final BufferedImage next;
            if (nextCursorIndex < entries[nextIndex].cursorPositions.length) {
                final Point cursorPos = entries[nextIndex].cursorPositions[nextCursorIndex];
                next = ImageUtils.drawCursor(getImage(nextIndex), cursorPos);
            } else {
                next = getImage(nextIndex);
            }

            ++nextCursorIndex;
            if (nextCursorIndex >= entries[nextIndex].cursorPositions.length) {
                ++count;
                nextCursorIndex = 0;
            }
            return next;
        }

        private BufferedImage getImage(final int index) {
            try {
                return ImageIO.read(entries[index].imagePath.toFile());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static class Entry {

        static final Point[] NO_CURSOR = new Point[]{};

        @NotNull
        final Path imagePath;
        @NotNull
        Point[] cursorPositions;

        Entry(@NotNull final Path imagePath) {
            this.imagePath = imagePath;
            this.cursorPositions = NO_CURSOR;
        }
    }
}

package screenrecorder.util;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
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

public class CircularImageBuffer implements Iterable<BufferedImage> {

    @NotNull
    private final Path tmpImagesDir;
    @NotNull
    private final List<Path> pathsToImages;
    @NotNull
    private final ExecutorService ioExecutor;
    private final long imagesSizeLimit;

    private int nextImagePathIndex;
    private long imagesSize;
    private int startIndex;
    private int endIndex;

    private CircularImageBuffer(final long sizeLimit) {
        try {
            tmpImagesDir = Files.createTempDirectory(getClass().getSimpleName());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        pathsToImages = new ArrayList<>();
        ioExecutor = Executors.newSingleThreadExecutor();
        imagesSizeLimit = sizeLimit;
        startIndex = -1;
        endIndex = -1;
    }

    @NotNull
    public static CircularImageBuffer newBuffer(final long sizeLimit) {
        return new CircularImageBuffer(sizeLimit);
    }

    public void putAsync(@NotNull final BufferedImage image) {
        if (ioExecutor.isShutdown()) {
            throw new IllegalStateException();
        }
        ioExecutor.execute(() -> {
            final byte[] bytes = ImageUtils.toByteArray(image);
            final Path outPath = getNextPathToStore(bytes.length);
            try (final OutputStream out = Files.newOutputStream(outPath, StandardOpenOption.WRITE)) {
                out.write(bytes);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @NotNull
    private Path getNextPathToStore(final int dataSize) {
        createFileIfNeeded(nextImagePathIndex);
        final Path next = pathsToImages.get(nextImagePathIndex);
        updateIndices(dataSize);
        return next;
    }

    private void updateIndices(final int dataSize) {
        if (dataSize + imagesSize > imagesSizeLimit) {
            nextImagePathIndex = (nextImagePathIndex + 1) % pathsToImages.size();
            startIndex = (startIndex + 1) % pathsToImages.size();
            endIndex = (endIndex + 1) % pathsToImages.size();
        } else {
            imagesSize += dataSize;
            ++nextImagePathIndex;
            if (startIndex == -1) {
                startIndex = 0;
            }
            ++endIndex;
        }
    }

    private void createFileIfNeeded(final int nextPathIndex) {
        if (nextPathIndex >= pathsToImages.size()) {
            try {
                pathsToImages.add(Files.createFile(Paths.get(tmpImagesDir.toString(), String.valueOf(nextPathIndex))));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private void awaitAllTasksCompletion() {
        try {
            ioExecutor.shutdown();
            ioExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteImages() {
        Log.i("deleting images...");
        awaitAllTasksCompletion();
        FileUtils.deleteDir(tmpImagesDir);
    }

    @NotNull
    @Override
    public Iterator<BufferedImage> iterator() {
        awaitAllTasksCompletion();
        return new ImageIterator(startIndex);
    }

    private class ImageIterator implements Iterator<BufferedImage> {

        private int nextIndex;

        ImageIterator(final int startIndex) {
            nextIndex = startIndex;
        }

        @Override
        public boolean hasNext() {
            return nextIndex != endIndex;
        }

        @Override
        public BufferedImage next() {
            final BufferedImage next = getImage(nextIndex);
            nextIndex = (nextIndex + 1) % pathsToImages.size();
            return next;
        }

        private BufferedImage getImage(final int index) {
            try {
                return ImageIO.read(pathsToImages.get(index).toFile());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}

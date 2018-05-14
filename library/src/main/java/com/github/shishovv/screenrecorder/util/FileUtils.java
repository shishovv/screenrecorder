package com.github.shishovv.screenrecorder.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    private FileUtils() {}

    public static void deleteDir(@NotNull Path dir) {
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException();
        }
        try {
            Files.walkFileTree(dir, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void createFileIfNotExists(@NotNull final Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void createDirsIfNotExists(@NotNull final Path path) {
        if (!Files.exists(path)) {
            try {
                if (Files.isDirectory(path)) {
                    Files.createDirectories(path);
                } else if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent()) ;
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}

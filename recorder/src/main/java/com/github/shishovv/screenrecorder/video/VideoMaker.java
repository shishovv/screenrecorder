package com.github.shishovv.screenrecorder.video;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public interface VideoMaker {

    void makeVideoAndSave(@NotNull Iterable<BufferedImage> images, @NotNull Path outPath);
}

package com.github.shishovv.screenrecorder.util;

import java.util.Objects;

public class Require {

    private Require() {}

    public static void require(final boolean condition, final String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

    public static void require(final boolean condition) {
        require(condition, "");
    }

    public static void requireNotNull(final Object o, final String message) {
        Objects.requireNonNull(o, message);
    }
}

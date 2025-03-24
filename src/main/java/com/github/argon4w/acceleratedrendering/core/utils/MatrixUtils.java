package com.github.argon4w.acceleratedrendering.core.utils;

import org.joml.Matrix4f;

public class MatrixUtils {

    public static boolean equals(Matrix4f a, Matrix4f b, float delta) {
        return (a == b) || (a != null && a.equals(b, delta));
    }
}

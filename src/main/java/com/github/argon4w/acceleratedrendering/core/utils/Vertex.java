package com.github.argon4w.acceleratedrendering.core.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {

    private final Vector3f position;
    private final int color;
    private final Vector2f uv;
    private final Vector3f normal;

    public Vertex(
            Vector3f position,
            int color,
            Vector2f uv,
            Vector3f normal
    ) {
        this.position = position;
        this.color = color;
        this.uv = uv;
        this.normal = normal;
    }

    public Vertex(
            Vector3f position,
            float u,
            float v
    ) {
        this.position = position;
        this.color = -1;
        this.uv = new Vector2f(u, v);
        this.normal = new Vector3f(0, 1, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getColor() {
        return color;
    }

    public Vector2f getUv() {
        return uv;
    }

    public Vector3f getNormal() {
        return normal;
    }
}

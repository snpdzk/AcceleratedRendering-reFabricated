package com.github.argon4w.acceleratedrendering.core.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {

    private final Vector3f position;
    private final Vector2f uv;
    private final Vector3f normal;
    private final int color;
    private final int light;

    public Vertex(
            Vector3f position,
            Vector2f uv,
            Vector3f normal,
            int color,
            int light
    ) {
        this.position = position;
        this.uv = uv;
        this.normal = normal;
        this.color = color;
        this.light = light;
    }

    public Vertex(
            Vector3f position,
            float u,
            float v
    ) {
        this.position = position;
        this.uv = new Vector2f(u, v);
        this.normal = new Vector3f();
        this.color = -1;
        this.light = 0;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getUv() {
        return uv;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public int getColor() {
        return color;
    }

    public int getLight() {
        return light;
    }
}

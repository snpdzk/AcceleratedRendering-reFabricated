package com.github.argon4w.acceleratedrendering.core.utils;

public class EmptyUVMapper implements IUVMapper {

    public static final EmptyUVMapper INSTANCE = new EmptyUVMapper();

    @Override
    public float mapU(float u) {
        return u;
    }

    @Override
    public float mapV(float v) {
        return v;
    }
}

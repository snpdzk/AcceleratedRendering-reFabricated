package com.github.argon4w.acceleratedrendering.core.utils;

public class UVUtils {

    public static IUVMapper getMapper(Object o) {
        return o instanceof IUVMapper mapper ? mapper : EmptyUVMapper.INSTANCE;
    }
}

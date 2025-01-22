package com.github.argon4w.acceleratedrendering.core.buffers;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleResetPool<T> {

    private final int size;
    private final Object[] pool;
    private final Consumer<T> resetter;

    private int cursor;

    public SimpleResetPool(
            int size,
            Supplier<T> initializer,
            Consumer<T> resetter
    ) {
        this.size = size;
        this.pool = new Object[size];
        this.resetter = resetter;

        this.cursor = 0;

        for (int i = 0; i < this.size; i++) {
            this.pool[i] = initializer.get();
        }
    }

    @SuppressWarnings("unchecked")
    public T get() {
        if (cursor < size) {
            return (T) pool[cursor ++];
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public void reset() {
        for (int i = 0; i < cursor; i++) {
            resetter.accept((T) pool[i]);
        }

        cursor = 0;
    }
}
package com.github.argon4w.acceleratedrendering.core.buffers.pools;

public abstract class SimpleResetPool<T> {

    private final int size;
    private final Object[] pool;

    private int cursor;

    public SimpleResetPool(int size) {
        this.size = size;
        this.pool = new Object[size];

        this.cursor = 0;

        for (int i = 0; i < this.size; i++) {
            this.pool[i] = create();
        }
    }

    protected abstract T create();
    protected abstract void reset(T t);

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
            reset((T) pool[i]);
        }

        cursor = 0;
    }

}
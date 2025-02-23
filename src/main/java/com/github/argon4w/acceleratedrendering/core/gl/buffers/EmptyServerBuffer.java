package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import java.nio.ByteBuffer;

public class EmptyServerBuffer implements IServerBuffer {

    public static final EmptyServerBuffer INSTANCE = new EmptyServerBuffer();

    @Override
    public int getBufferHandle() {
        return 0;
    }

    @Override
    public void bind(int target) {

    }

    @Override
    public void subData(long offset, int[] data) {

    }

    @Override
    public void bindBase(int target, int index) {

    }

    @Override
    public void bindRange(
            int target,
            int index,
            long offset,
            long size
    ) {

    }

    @Override
    public void clear(
            long offset,
            long size,
            ByteBuffer buffer
    ) {

    }

    @Override
    public void clear(
            long offset,
            long size,
            int value
    ) {

    }
}

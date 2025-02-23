package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import java.nio.ByteBuffer;

public interface IServerBuffer {

    int getBufferHandle();
    void subData(long offset, int[] data);
    void bind(int target);
    void bindBase(int target, int index);
    void bindRange(int target, int index, long offset, long size);
    void clear(long offset, long size, ByteBuffer buffer);
    void clear(long offset, long size, int value);
}

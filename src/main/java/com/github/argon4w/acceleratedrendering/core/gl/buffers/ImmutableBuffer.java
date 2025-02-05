package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import static org.lwjgl.opengl.GL46.*;

public class ImmutableBuffer implements IServerBuffer {

    protected final int bufferHandle;

    public ImmutableBuffer(long size, int bits) {
        this.bufferHandle = glCreateBuffers();
        glNamedBufferStorage(bufferHandle, size, bits);
    }

    public ImmutableBuffer(int bits, int[] data) {
        this.bufferHandle = glCreateBuffers();
        glNamedBufferStorage(bufferHandle, data, bits);
    }

    public void copyTo(IServerBuffer buffer, long size) {
        glCopyNamedBufferSubData(bufferHandle, buffer.getBufferHandle(), 0, 0, size);
    }

    public long map(int bits) {
        return nglMapNamedBuffer(bufferHandle, bits);
    }

    public void unmap() {
        glUnmapNamedBuffer(bufferHandle);
    }

    public void delete() {
        glDeleteBuffers(bufferHandle);
    }

    @Override
    public void bind(int target) {
        glBindBuffer(target, bufferHandle);
    }

    @Override
    public void bindBase(int target, int index) {
        glBindBufferBase(target, index, bufferHandle);
    }

    @Override
    public int getBufferHandle() {
        return bufferHandle;
    }
}

package com.github.argon4w.acceleratedrendering.gl;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class ImmutableGLBuffer implements IGLBuffer {

    private final int bufferHandle;

    public ImmutableGLBuffer(long size, int bits) {
        this.bufferHandle = glCreateBuffers();
        glNamedBufferStorage(bufferHandle, size, bits);
    }

    public void bind(int target) {
        glBindBuffer(target, bufferHandle);
    }

    public void setData(int offset, ByteBuffer data) {
        glNamedBufferSubData(bufferHandle, offset, data);
    }

    public void setData(ByteBuffer data) {
        glNamedBufferSubData(bufferHandle, 0, data);
    }

    public void copyTo(ImmutableGLBuffer buffer, long readOffset, long writeOffset, long size) {
        glCopyNamedBufferSubData(bufferHandle, buffer.bufferHandle, readOffset, writeOffset, size);
    }

    public void copyTo(ImmutableGLBuffer buffer, long size) {
        glCopyNamedBufferSubData(bufferHandle, buffer.bufferHandle, 0, 0, size);
    }

    public void copyFrom(ImmutableGLBuffer buffer, long readOffset,long writeOffset, long size ) {
        glCopyNamedBufferSubData(buffer.bufferHandle, bufferHandle, readOffset, writeOffset, size);
    }

    public void copyFrom(ImmutableGLBuffer buffer, long size) {
        glCopyNamedBufferSubData(buffer.bufferHandle, bufferHandle, 0, 0, size);
    }

    public ByteBuffer map(int bits) {
        return glMapNamedBuffer(bufferHandle, bits);
    }

    public ByteBuffer map(int bits, ByteBuffer oldBuffer) {
        return glMapNamedBuffer(bufferHandle, bits, oldBuffer);
    }

    public void unmap() {
        glUnmapNamedBuffer(bufferHandle);
    }

    public void delete() {
        glDeleteBuffers(bufferHandle);
    }

    @Override
    public int getBufferHandle() {
        return bufferHandle;
    }
}

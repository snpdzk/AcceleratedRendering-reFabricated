package com.github.argon4w.acceleratedrendering.core.gl;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class ImmutableBuffer implements IServerBuffer {

    private final int bufferHandle;

    public ImmutableBuffer(long size, int bits) {
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

    public void copyTo(IServerBuffer buffer, long readOffset, long writeOffset, long size) {
        glCopyNamedBufferSubData(bufferHandle, buffer.getBufferHandle(), readOffset, writeOffset, size);
    }

    public void copyTo(IServerBuffer buffer, long size) {
        glCopyNamedBufferSubData(bufferHandle, buffer.getBufferHandle(), 0, 0, size);
    }

    public void copyFrom(IServerBuffer buffer, long readOffset, long writeOffset, long size) {
        glBindBuffer(GL_COPY_READ_BUFFER, buffer.getBufferHandle());
        glBindBuffer(GL_COPY_WRITE_BUFFER, bufferHandle);
        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);
    }

    public void copyFrom(IServerBuffer buffer, long size) {
        glCopyNamedBufferSubData(buffer.getBufferHandle(), bufferHandle, 0, 0, size);
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

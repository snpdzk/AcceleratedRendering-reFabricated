package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL46.*;

public class ImmutableBuffer implements IServerBuffer {

    protected final int bufferHandle;

    public ImmutableBuffer(long size, int bits) {
        this.bufferHandle = glCreateBuffers();

        glNamedBufferStorage(
                bufferHandle,
                size,
                bits
        );
    }

    public ImmutableBuffer(int bits, int[] data) {
        this.bufferHandle = glCreateBuffers();

        glNamedBufferStorage(
                bufferHandle,
                data,
                bits
        );
    }

    public void copyTo(IServerBuffer buffer, long size) {
        glCopyNamedBufferSubData(
                bufferHandle,
                buffer.getBufferHandle(),
                0,
                0,
                size
        );
    }

    public long map(long length, int bits) {
        return nglMapNamedBufferRange(
                bufferHandle,
                0L,
                length,
                bits
        );
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

    @Override
    public void bind(int target) {
        glBindBuffer(target, bufferHandle);
    }

    @Override
    public void clearInteger(long offset, int value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            BufferClearType.INTEGER.clear(
                    bufferHandle,
                    offset,
                    Integer.BYTES,
                    stack.malloc(4).putInt(0, value)
            );
        }
    }

    @Override
    public void clearBytes(long offset, long size) {
        BufferClearType.BYTE.clear(
                bufferHandle,
                offset,
                size,
                null
        );
    }

    @Override
    public void subData(long offset, int[] data) {
        glNamedBufferSubData(
                bufferHandle,
                offset,
                data
        );
    }

    @Override
    public void bindBase(int target, int index) {
        glBindBufferBase(
                target,
                index,
                bufferHandle
        );
    }

    @Override
    public void bindRange(
            int target,
            int index,
            long offset,
            long size
    ) {
        glBindBufferRange(
                target,
                index,
                bufferHandle,
                offset,
                size
        );
    }
}

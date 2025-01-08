package com.github.argon4w.acceleratedrendering.gl;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class ResizableMappedGLBuffer {

    private static final int BITS = GL_DYNAMIC_STORAGE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_WRITE_BIT;

    private ImmutableGLBuffer glBuffer;
    private ByteBuffer mappedBuffer;
    private long bufferAddress;

    private long bufferSize;
    private long bufferPosition;
    private boolean mapped;

    public ResizableMappedGLBuffer(long initialSize) {
        this.glBuffer = new ImmutableGLBuffer(initialSize, BITS);
        this.mappedBuffer = this.glBuffer.map(GL_WRITE_ONLY);
        this.bufferAddress = MemoryUtil.memAddress0(this.mappedBuffer);

        this.bufferSize = initialSize;
        this.bufferPosition = 0;
        this.mapped = true;
    }

    public long reserve(long bytes) {
        if (!mapped) {
            throw new IllegalStateException("Buffer is not mapped");
        }

        long position = bufferPosition;
        bufferPosition = position + bytes;

        if (bufferPosition <= bufferSize) {
            return bufferAddress + position;
        }

        long newBufferSize = bufferSize * 2;
        ImmutableGLBuffer newBuffer = new ImmutableGLBuffer(newBufferSize, BITS);

        glBuffer.unmap();
        glBuffer.copyTo(newBuffer, bufferSize);
        glBuffer.delete();

        mappedBuffer = newBuffer.map(GL_WRITE_ONLY, mappedBuffer);
        bufferAddress = MemoryUtil.memAddress0(mappedBuffer);
        glBuffer = newBuffer;
        bufferSize = newBufferSize;

        return bufferAddress + position;
    }

    public void map() {
        if (!mapped) {
            mappedBuffer = glBuffer.map(GL_WRITE_ONLY, mappedBuffer);
            bufferAddress = MemoryUtil.memAddress0(mappedBuffer);
            mapped = true;
        }
    }

    public void unmap() {
        if (mapped) {
            glBuffer.unmap();
            mapped = false;
        }
    }

    public void delete() {
        glBuffer.unmap();
        glBuffer.delete();
    }

    public void reset() {
        bufferPosition = 0;
    }

    public long getPosition() {
        return bufferPosition;
    }

    public int getBufferHandle() {
        return glBuffer.getBufferHandle();
    }
}

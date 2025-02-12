package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class MappedBuffer extends MutableBuffer implements IClientBuffer {

    private long bufferAddress;
    private long bufferPosition;

    public MappedBuffer(long initialSize) {
        super(initialSize, GL_DYNAMIC_STORAGE_BIT
                | GL_MAP_PERSISTENT_BIT
                | GL_MAP_COHERENT_BIT
                | GL_MAP_WRITE_BIT);
        this.bufferAddress = map();
    }

    @Override
    public long reserve(long bytes) {
        long position = bufferPosition;
        bufferPosition = position + bytes;

        if (bufferPosition <= bufferSize) {
            return bufferAddress + position;
        }

        resize(bufferPosition);
        return bufferAddress + position;
    }

    @Override
    public ByteBuffer asByteBuffer() {
        return MemoryUtil.memByteBuffer(bufferAddress, (int) getBufferSize());
    }

    public void resizeTo(long newBufferSize) {
        unmap();
        super.resizeTo(newBufferSize);
        bufferAddress = map();
    }

    public void delete() {
        unmap();
        super.delete();
    }

    public long map() {
        return map(GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT);
    }

    public void reset() {
        bufferPosition = 0;
    }

    public long getPosition() {
        return bufferPosition;
    }
}

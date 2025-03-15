package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import com.github.argon4w.acceleratedrendering.core.utils.ByteUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class MappedBuffer extends MutableBuffer implements IClientBuffer {

    protected long address;
    protected long position;

    public MappedBuffer(long initialSize) {
        super(initialSize, GL_DYNAMIC_STORAGE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_WRITE_BIT);
        this.address = map();
    }

    @Override
    public long reserve(long bytes) {
        long position = this.position;
        this.position += bytes;

        if (this.position <= size) {
            return address + position;
        }

        resize(this.position);
        return address + position;
    }

    @Override
    public ByteBuffer byteBuffer() {
        return ByteUtils.toBuffer(address, size);
    }

    @Override
    public void beforeExpand() {
        unmap();
    }

    @Override
    public void afterExpand() {
        address = map();
    }

    @Override
    public void bind(int target) {
        throw new IllegalStateException("Buffer is mapped.");
    }

    public long map() {
        return map(GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT);
    }

    public void reset() {
        position = 0;
    }

    public long getPosition() {
        return position;
    }
}

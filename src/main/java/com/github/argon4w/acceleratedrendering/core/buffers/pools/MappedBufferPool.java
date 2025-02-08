package com.github.argon4w.acceleratedrendering.core.buffers.pools;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;

public class MappedBufferPool extends SimpleResetPool<MappedBuffer> {

    public MappedBufferPool(int size) {
        super(size);
    }

    @Override
    protected MappedBuffer create() {
        return new MappedBuffer(64L);
    }

    @Override
    protected void reset(MappedBuffer mappedBuffer) {
        mappedBuffer.reset();
    }

    @Override
    public void reset() {
        super.reset();
    }
}

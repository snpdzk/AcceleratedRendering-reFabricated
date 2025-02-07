package com.github.argon4w.acceleratedrendering.core.buffers.pools;

import com.github.argon4w.acceleratedrendering.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;

public class MappedBufferPool extends SimpleResetPool<MappedBuffer> {

    public MappedBufferPool() {
        super(CoreFeature.getPooledBufferSetSize());
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

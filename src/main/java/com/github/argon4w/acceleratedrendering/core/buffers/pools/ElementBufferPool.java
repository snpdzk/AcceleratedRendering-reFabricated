package com.github.argon4w.acceleratedrendering.core.buffers.pools;

import com.github.argon4w.acceleratedrendering.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSetPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;

public class ElementBufferPool extends SimpleResetPool<ElementBuffer, AcceleratedBufferSetPool.BufferSet> {

    public ElementBufferPool(AcceleratedBufferSetPool.BufferSet bufferSet) {
        super(CoreFeature.getPooledBufferSetSize(), bufferSet);
    }

    @Override
    protected ElementBuffer create(AcceleratedBufferSetPool.BufferSet bufferSet) {
        return new ElementBuffer(bufferSet);
    }

    @Override
    protected void reset(ElementBuffer elementBuffer) {
        elementBuffer.reset();
    }

    @Override
    public void reset() {
        super.reset();
    }
}

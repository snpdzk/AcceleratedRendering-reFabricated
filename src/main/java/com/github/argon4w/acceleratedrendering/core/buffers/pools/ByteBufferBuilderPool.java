package com.github.argon4w.acceleratedrendering.core.buffers.pools;

import com.github.argon4w.acceleratedrendering.CoreFeature;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;

public class ByteBufferBuilderPool extends SimpleResetPool<ByteBufferBuilder> {

    public ByteBufferBuilderPool() {
        super(CoreFeature.getPooledBufferSetSize());
    }

    @Override
    protected ByteBufferBuilder create() {
        return new ByteBufferBuilder(1024);
    }

    @Override
    protected void reset(ByteBufferBuilder byteBufferBuilder) {
        byteBufferBuilder.clear();
    }

    @Override
    public void reset() {
        super.reset();
    }
}

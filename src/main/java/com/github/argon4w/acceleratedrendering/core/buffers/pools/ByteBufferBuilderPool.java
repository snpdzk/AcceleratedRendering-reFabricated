package com.github.argon4w.acceleratedrendering.core.buffers.pools;

import com.github.argon4w.acceleratedrendering.CoreFeature;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;

public class ByteBufferBuilderPool extends SimpleResetPool<ByteBufferBuilder, Integer> {

    public ByteBufferBuilderPool() {
        super(CoreFeature.getPooledBufferSetSize(), 1024);
    }

    @Override
    protected ByteBufferBuilder create(Integer context) {
        return new ByteBufferBuilder(context);
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

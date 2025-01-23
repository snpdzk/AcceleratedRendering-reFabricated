package com.github.argon4w.acceleratedrendering.core.buffers.accelerated;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.IntElementUtils;
import com.mojang.blaze3d.vertex.VertexFormat;

public class ElementBuffer extends MappedBuffer {

    private final AcceleratedBufferSetPool.BufferSet bufferSet;

    private VertexFormat.Mode mode;

    public ElementBuffer(AcceleratedBufferSetPool.BufferSet bufferSet) {
        super(64L);

        this.bufferSet = bufferSet;
        this.mode = null;
    }

    public void reserveElements(int vertexCount) {
        IntElementUtils.putElements(
                mode,
                this,
                bufferSet.getElement(vertexCount),
                vertexCount
        );
    }

    public ElementBuffer setMode(VertexFormat.Mode mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public void reset() {
        super.reset();
        this.mode = null;
    }
}

package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.IntIndexUtils;
import com.mojang.blaze3d.vertex.VertexFormat;

public class ElementBuffer extends MappedBuffer {

    private final VertexFormat.Mode mode;
    private final AcceleratedBufferSetPool.BufferSet bufferSet;

    public ElementBuffer(
            VertexFormat.Mode mode,
            AcceleratedBufferSetPool.BufferSet bufferSet
    ) {
        super(1024L);

        this.mode = mode;
        this.bufferSet = bufferSet;
    }

    public void reserveElements(int vertexCount) {
        IntIndexUtils.putIndices(
                mode,
                this,
                bufferSet.getElement(vertexCount),
                vertexCount
        );
    }
}

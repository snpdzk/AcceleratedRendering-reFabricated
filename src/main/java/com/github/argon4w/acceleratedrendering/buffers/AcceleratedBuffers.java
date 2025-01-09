package com.github.argon4w.acceleratedrendering.buffers;

import com.github.argon4w.acceleratedrendering.utils.ByteBufferUtils;
import com.github.argon4w.acceleratedrendering.utils.IndexUtils;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

public class AcceleratedBuffers {

    private final ByteBufferBuilder indexBufferBuilder;
    private final AcceleratedBufferSource bufferSource;

    public AcceleratedBuffers(AcceleratedBufferSource bufferSource) {
        this.indexBufferBuilder = new ByteBufferBuilder(4 * 1024);
        this.bufferSource = bufferSource;
    }

    public void clear() {
        indexBufferBuilder.clear();
    }

    public ByteBufferBuilder.Result buildIndexBuffer(int vertices) {
        return IndexUtils.buildIndexBuffer(vertices, indexBufferBuilder.build(), indexBufferBuilder);
    }

    public long reservePose() {
        return bufferSource.getPoseBuffer().reserve(4L * 4L * 4L + 4L * 4L * 3L);
    }

    public long reserveVertex() {
        return bufferSource.getVertexBuffer().reserve(DefaultVertexFormat.NEW_ENTITY.getVertexSize());
    }

    public long reserveVertices(long count) {
        return bufferSource.getVertexBuffer().reserve(DefaultVertexFormat.NEW_ENTITY.getVertexSize() * count);
    }

    public long reserveVarying() {
        return bufferSource.getVaryingBuffer().reserve(4L * 4L);
    }

    public long reserveVaryings(long count) {
        return bufferSource.getVaryingBuffer().reserve(4L * 4L * count);
    }

    public void reserveIndex() {
        ByteBufferUtils.putInt(indexBufferBuilder.reserve(4), bufferSource.getIndex());
    }

    public int getPose() {
        return bufferSource.getPose();
    }
}

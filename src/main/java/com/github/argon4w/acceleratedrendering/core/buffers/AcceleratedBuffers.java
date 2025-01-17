package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.IntIndexUtils;
import com.mojang.blaze3d.vertex.VertexFormat;

public class AcceleratedBuffers implements IAcceleratedBuffers {

    private final MappedBuffer indexBuffer;
    private final IAcceleratedBufferSource bufferSource;
    private final VertexFormat.Mode mode;
    private final int vertexSize;

    public AcceleratedBuffers(IAcceleratedBufferSource bufferSource, VertexFormat.Mode mode) {
        this.indexBuffer = new MappedBuffer(4L * 1024L);
        this.bufferSource = bufferSource;
        this.mode = mode;
        this.vertexSize = bufferSource.getBufferEnvironment().getVertexSize();
    }

    @Override
    public void clear() {
        indexBuffer.reset();
    }

    @Override
    public long reserveSharings() {
        return bufferSource.getSharingBuffer().reserve(4L * 4L * 4L + 4L * 4L * 3L + 4L * 4L);
    }

    @Override
    public long reserveVertex() {
        return bufferSource.getVertexBuffer().reserve(vertexSize);
    }

    @Override
    public long reserveVertices(long count) {
        return bufferSource.getVertexBuffer().reserve(vertexSize * count);
    }

    @Override
    public long reserveVarying() {
        return bufferSource.getVaryingBuffer().reserve(5L * 4L);
    }

    @Override
    public long reserveVaryings(long count) {
        return bufferSource.getVaryingBuffer().reserve(5L * 4L * count);
    }

    @Override
    public void reserveIndices(int vertexCount) {
        IntIndexUtils.putIndices(mode, indexBuffer, bufferSource.getIndex(vertexCount), vertexCount);
    }

    @Override
    public MappedBuffer getIndexBuffer() {
        return indexBuffer;
    }

    @Override
    public int getSharing() {
        return bufferSource.getSharing();
    }
}

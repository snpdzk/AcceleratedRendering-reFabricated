package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;

public interface IAcceleratedBuffers {

    void clear();
    long reserveSharings();
    long reserveVertex();
    long reserveVertices(long count);
    long reserveVarying();
    long reserveVaryings(long count);
    void reserveIndices(int vertexCount);
    MappedBuffer getIndexBuffer();
    int getSharing();
}

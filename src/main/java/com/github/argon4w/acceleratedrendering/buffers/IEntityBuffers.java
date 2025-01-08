package com.github.argon4w.acceleratedrendering.buffers;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;

public interface IEntityBuffers {
    VertexConsumer newBufferBuilder();
    ByteBufferBuilder.Result buildIndexBuffer(int count);
    long reservePose();
    long reserveVertex();
    long reserveVertices(long count);
    long reserveVarying();
    long reserveVaryings(long count);
    void reserveIndex();
    int getPose();
    void clear();
}

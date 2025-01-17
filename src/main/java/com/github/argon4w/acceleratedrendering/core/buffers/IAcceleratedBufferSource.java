package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public interface IAcceleratedBufferSource {

    VertexConsumer getBuffer(RenderType type);
    IBufferEnvironment getBufferEnvironment();
    MappedBuffer getVertexBuffer();
    MappedBuffer getVaryingBuffer();
    MappedBuffer getSharingBuffer();
    void drawBuffers();
    void clearBuffers();
    int getSharing();
    int getIndex(int count);
}

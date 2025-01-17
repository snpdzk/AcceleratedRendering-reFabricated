package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

public interface IAcceleratedBufferSource {

    VertexConsumer getBuffer(RenderType type);
    IBufferEnvironment getBufferEnvironment();
    void drawBuffers();
    void clearBuffers();
}

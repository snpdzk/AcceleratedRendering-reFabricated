package com.github.argon4w.acceleratedrendering.core.buffers.graphs;

import net.minecraft.client.renderer.RenderType;

public interface IBufferGraph {

    float mapU(float u);
    float mapV(float v);
    RenderType getRenderType();
}

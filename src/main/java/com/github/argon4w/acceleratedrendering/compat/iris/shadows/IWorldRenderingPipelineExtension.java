package com.github.argon4w.acceleratedrendering.compat.iris.shadows;

import net.minecraft.client.renderer.MultiBufferSource;

public interface IWorldRenderingPipelineExtension {

    MultiBufferSource.BufferSource getShadowBufferSource();
}

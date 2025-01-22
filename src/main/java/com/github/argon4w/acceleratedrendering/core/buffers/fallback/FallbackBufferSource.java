package com.github.argon4w.acceleratedrendering.core.buffers.fallback;

import com.github.argon4w.acceleratedrendering.CoreFeature;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class FallbackBufferSource implements MultiBufferSource {

    private final MultiBufferSource vanillaBufferSource;
    private final MultiBufferSource vanillaBatchingBufferSource;

    public FallbackBufferSource(
            MultiBufferSource vanillaBufferSource,
            MultiBufferSource vanillaBatchingBufferSource) {
        this.vanillaBufferSource = vanillaBufferSource;
        this.vanillaBatchingBufferSource = vanillaBatchingBufferSource;
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        return CoreFeature.shouldUseVanillaBatching()
                ? vanillaBatchingBufferSource.getBuffer(pRenderType)
                : vanillaBufferSource.getBuffer(pRenderType);
    }
}

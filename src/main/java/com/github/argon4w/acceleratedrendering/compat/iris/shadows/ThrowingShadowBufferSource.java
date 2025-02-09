package com.github.argon4w.acceleratedrendering.compat.iris.shadows;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class ThrowingShadowBufferSource extends MultiBufferSource.BufferSource {

    public static final ThrowingShadowBufferSource INSTANCE = new ThrowingShadowBufferSource();

    public ThrowingShadowBufferSource() {
        super(null, null);
    }

    @Override
    public void endLastBatch() {
        throw new IllegalStateException("Shadow rendered when Shadow Renderer is not present.");
    }

    @Override
    public void endBatch() {
        throw new IllegalStateException("Shadow rendered when Shadow Renderer is not present.");
    }

    @Override
    public void endBatch(RenderType pRenderType) {
        throw new IllegalStateException("Shadow rendered when Shadow Renderer is not present.");
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        throw new IllegalStateException("Shadow rendered when Shadow Renderer is not present.");
    }
}

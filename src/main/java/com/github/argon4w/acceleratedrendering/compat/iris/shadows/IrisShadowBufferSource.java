package com.github.argon4w.acceleratedrendering.compat.iris.shadows;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.irisshaders.iris.Iris;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class IrisShadowBufferSource extends MultiBufferSource.BufferSource {

    public static final IrisShadowBufferSource INSTANCE = new IrisShadowBufferSource();

    public IrisShadowBufferSource() {
        super(null, null);
    }

    @Override
    public void endLastBatch() {
        ((IWorldRenderingPipelineExtension) Iris.getPipelineManager().getPipelineNullable()).getShadowBufferSource().endLastBatch();
    }

    @Override
    public void endBatch() {
        ((IWorldRenderingPipelineExtension) Iris.getPipelineManager().getPipelineNullable()).getShadowBufferSource().endBatch();
    }

    @Override
    public void endBatch(RenderType pRenderType) {
        ((IWorldRenderingPipelineExtension) Iris.getPipelineManager().getPipelineNullable()).getShadowBufferSource().endBatch(pRenderType);
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        return ((IWorldRenderingPipelineExtension) Iris.getPipelineManager().getPipelineNullable()).getShadowBufferSource().getBuffer(pRenderType);
    }
}

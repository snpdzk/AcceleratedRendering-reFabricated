package com.github.argon4w.acceleratedrendering.core.buffers.outline;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedDoubleVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.Optional;

public class SimpleOutlineBufferSource implements MultiBufferSource {

    private final MultiBufferSource bufferSource;
    private final IOutlineBufferSource outlineBufferSource;

    public SimpleOutlineBufferSource(MultiBufferSource bufferSource, IOutlineBufferSource outlineBufferSource) {
        this.bufferSource = bufferSource;
        this.outlineBufferSource = outlineBufferSource;
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        if (pRenderType.isOutline()) {
            return outlineBufferSource.getBuffer(pRenderType);
        }

        VertexConsumer buffer = bufferSource.getBuffer(pRenderType);
        Optional<RenderType> outline = pRenderType.outline();

        if (outline.isEmpty()) {
            return buffer;
        }

        return new AcceleratedDoubleVertexConsumer(buffer, outlineBufferSource.getBuffer(outline.get()));
    }

    public SimpleOutlineBufferSource setColor(int color) {
        outlineBufferSource.setColor(color);
        return this;
    }
}

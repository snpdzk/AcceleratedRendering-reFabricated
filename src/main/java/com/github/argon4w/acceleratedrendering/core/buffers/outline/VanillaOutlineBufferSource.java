package com.github.argon4w.acceleratedrendering.core.buffers.outline;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;

public class VanillaOutlineBufferSource implements IOutlineBufferSource {

    private final OutlineBufferSource outlineBufferSource;

    public VanillaOutlineBufferSource(OutlineBufferSource outlineBufferSource) {
        this.outlineBufferSource = outlineBufferSource;
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        return outlineBufferSource.getBuffer(pRenderType);
    }

    @Override
    public void setColor(int color) {
        outlineBufferSource.setColor(
                FastColor.ARGB32.red(color),
                FastColor.ARGB32.green(color),
                FastColor.ARGB32.blue(color),
                255
        );
    }
}

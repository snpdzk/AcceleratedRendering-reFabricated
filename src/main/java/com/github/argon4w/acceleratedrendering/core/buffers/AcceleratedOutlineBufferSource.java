package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedDoubleVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedOutlineGenerator;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

import java.util.Optional;

public class AcceleratedOutlineBufferSource extends AcceleratedBufferSource {

    public static final AcceleratedOutlineBufferSource OUTLINE = new AcceleratedOutlineBufferSource(CORE);

    private final IAcceleratedBufferSource bufferSource;
    private int teamColor;

    public AcceleratedOutlineBufferSource(IAcceleratedBufferSource bufferSource) {
        super(
                DefaultVertexFormat.POSITION_TEX_COLOR,
                ComputeShaderPrograms.CORE_POS_TEX_COLOR_COMPUTE_SHADER_KEY);
        this.bufferSource = bufferSource;
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        if (pRenderType.isOutline()) {
            return new AcceleratedOutlineGenerator(super.getBuffer(pRenderType), pRenderType, teamColor);
        }

        VertexConsumer buffer = this.bufferSource.getBuffer(pRenderType);
        Optional<RenderType> outlineRenderType = pRenderType.outline();

        if (outlineRenderType.isEmpty()) {
            return buffer;
        }

        return new AcceleratedDoubleVertexConsumer(
                pRenderType, buffer,
                outlineRenderType.get(), new AcceleratedOutlineGenerator(super.getBuffer(outlineRenderType.get()), pRenderType, teamColor)
        );
    }

    public AcceleratedOutlineBufferSource setTeamColor(int color) {
        this.teamColor = color;
        return this;
    }
}

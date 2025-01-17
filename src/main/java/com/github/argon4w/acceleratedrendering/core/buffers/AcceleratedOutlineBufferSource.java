package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedDoubleVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedOutlineGenerator;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

import java.util.Optional;

public class AcceleratedOutlineBufferSource extends AcceleratedBufferSource {

    public static final AcceleratedOutlineBufferSource OUTLINE = initOutlineBufferSource();

    public static AcceleratedOutlineBufferSource initOutlineBufferSource() {
        return new AcceleratedOutlineBufferSource(
                CORE,
                IBufferEnvironment.OUTLINE
        );
    }

    private final IAcceleratedBufferSource bufferSource;
    private int teamColor;

    public AcceleratedOutlineBufferSource(
            IAcceleratedBufferSource bufferSource,
            IBufferEnvironment bufferEnvironment
    ) {
        super(bufferEnvironment);
        this.bufferSource = bufferSource;
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        if (pRenderType.isOutline()) {
            return new AcceleratedOutlineGenerator(
                    bufferSource.getBufferEnvironment(),
                    super.getBuffer(pRenderType),
                    pRenderType,
                    teamColor
            );
        }

        VertexConsumer buffer = bufferSource.getBuffer(pRenderType);
        Optional<RenderType> outlineRenderType = pRenderType.outline();

        if (outlineRenderType.isEmpty()) {
            return buffer;
        }

        RenderType outline = outlineRenderType.get();
        AcceleratedOutlineGenerator generator = new AcceleratedOutlineGenerator(
                bufferSource.getBufferEnvironment(),
                super.getBuffer(outlineRenderType.get()),
                outline,
                teamColor
        );

        return new AcceleratedDoubleVertexConsumer(
                bufferSource.getBufferEnvironment(),
                pRenderType,
                buffer,
                outline,
                generator
        );
    }

    public AcceleratedOutlineBufferSource setTeamColor(int color) {
        teamColor = color;
        return this;
    }
}

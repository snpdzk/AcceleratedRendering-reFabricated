package com.github.argon4w.acceleratedrendering.compat.iris;

import com.github.argon4w.acceleratedrendering.compat.iris.shadows.IrisShadowBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingBufferSource;
import com.mojang.blaze3d.vertex.VertexFormat;

public class IrisCompatBuffers {

    public static final AcceleratedBufferSource POS_TEX_SHADOW = new AcceleratedBufferSource(IBufferEnvironment.Presets.POS_TEX);
    public static final AcceleratedBufferSource ENTITY_SHADOW = new AcceleratedBufferSource(IBufferEnvironment.Presets.ENTITY);

    public static final RedirectingBufferSource SHADOW = RedirectingBufferSource.builder()
            .fallback(IrisShadowBufferSource.INSTANCE)
            .bufferSource(ENTITY_SHADOW)
            .bufferSource(POS_TEX_SHADOW)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .mode(VertexFormat.Mode.LINES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();
}

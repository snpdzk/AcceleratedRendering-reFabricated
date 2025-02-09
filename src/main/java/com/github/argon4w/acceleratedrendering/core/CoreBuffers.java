package com.github.argon4w.acceleratedrendering.core;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.buffers.outline.OutlineMaskBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.outline.SimpleOutlineBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingOutlineBufferSource;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;

public class CoreBuffers {

    public static final AcceleratedBufferSource ENTITY = new AcceleratedBufferSource(IBufferEnvironment.Presets.ENTITY);
    public static final AcceleratedBufferSource POS_TEX = new AcceleratedBufferSource(IBufferEnvironment.Presets.POS_TEX);

    public static final AcceleratedBufferSource POS_TEX_COLOR = new AcceleratedBufferSource(IBufferEnvironment.Presets.POS_TEX_COLOR);
    public static final OutlineMaskBufferSource OUTLINE_MASK = new OutlineMaskBufferSource(POS_TEX_COLOR);

    public static final RedirectingBufferSource CORE = RedirectingBufferSource.builder()
            .fallback(Minecraft.getInstance().renderBuffers().bufferSource())
            .bufferSource(ENTITY)
            .bufferSource(POS_TEX)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .mode(VertexFormat.Mode.LINES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();

    public static final RedirectingOutlineBufferSource OUTLINE = RedirectingOutlineBufferSource.builder()
            .fallback(Minecraft.getInstance().renderBuffers().outlineBufferSource())
            .bufferSource(OUTLINE_MASK)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .mode(VertexFormat.Mode.LINES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();

    public static final SimpleOutlineBufferSource CORE_OUTLINE = new SimpleOutlineBufferSource(CORE, OUTLINE);
}

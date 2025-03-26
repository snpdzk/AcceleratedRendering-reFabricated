package com.github.argon4w.acceleratedrendering.core;

import com.github.argon4w.acceleratedrendering.core.buffers.RedirectingBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.SimpleOutlineBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;

public class CoreBuffers {

    public static final AcceleratedBufferSource BLOCK = new AcceleratedBufferSource(IBufferEnvironment.Presets.BLOCK);
    public static final AcceleratedBufferSource ENTITY = new AcceleratedBufferSource(IBufferEnvironment.Presets.ENTITY);
    public static final AcceleratedBufferSource POS_TEX = new AcceleratedBufferSource(IBufferEnvironment.Presets.POS_TEX);
    public static final AcceleratedBufferSource POS_COLOR_TEX_LIGHT = new AcceleratedBufferSource(IBufferEnvironment.Presets.POS_COLOR_TEX_LIGHT);
    public static final AcceleratedBufferSource POS_TEX_COLOR = new AcceleratedBufferSource(IBufferEnvironment.Presets.POS_TEX_COLOR);

    public static final RedirectingBufferSource CORE = RedirectingBufferSource.builder()
            .fallback(Minecraft.getInstance().renderBuffers().bufferSource())
            .bufferSource(BLOCK)
            .bufferSource(ENTITY)
            .bufferSource(POS_TEX)
            .bufferSource(POS_COLOR_TEX_LIGHT)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();

    public static final RedirectingBufferSource OUTLINE = RedirectingBufferSource.builder()
            .fallback(Minecraft.getInstance().renderBuffers().outlineBufferSource())
            .bufferSource(POS_TEX_COLOR)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();

    public static final SimpleOutlineBufferSource CORE_OUTLINE = new SimpleOutlineBufferSource(CORE, OUTLINE);
}

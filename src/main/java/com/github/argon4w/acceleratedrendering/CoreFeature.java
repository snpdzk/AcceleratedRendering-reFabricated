package com.github.argon4w.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.buffers.fallback.FallbackBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.fallback.FallbackOutlineBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.fallback.VanillaBatchingBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.outline.OutlineMaskBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.outline.SimpleOutlineBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingOutlineBufferSource;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;

public class CoreFeature {

    public static final VanillaBatchingBufferSource CORE_BATCHING = new VanillaBatchingBufferSource();
    public static final VanillaBatchingBufferSource OUTLINE_BATCHING = new VanillaBatchingBufferSource();
    public static final OutlineMaskBufferSource OUTLINE_BATCHING_MASK = new OutlineMaskBufferSource(OUTLINE_BATCHING);

    public static final FallbackBufferSource CORE_FALLBACK = new FallbackBufferSource(Minecraft.getInstance().renderBuffers().bufferSource(), CORE_BATCHING);
    public static final FallbackOutlineBufferSource OUTLINE_FALLBACK = new FallbackOutlineBufferSource(Minecraft.getInstance().renderBuffers().outlineBufferSource(), OUTLINE_BATCHING_MASK);

    public static final AcceleratedBufferSource ENTITY = new AcceleratedBufferSource(IBufferEnvironment.Presets.getEntityEnvironment());
    public static final AcceleratedBufferSource POS_TEX = new AcceleratedBufferSource(IBufferEnvironment.Presets.getPosTexEnvironment());

    public static final AcceleratedBufferSource POS_TEX_COLOR = new AcceleratedBufferSource(IBufferEnvironment.Presets.getPosTexColorEnvironment());
    public static final OutlineMaskBufferSource OUTLINE_MASK = new OutlineMaskBufferSource(POS_TEX_COLOR);

    public static final RedirectingBufferSource CORE = RedirectingBufferSource.builder()
            .fallback(CORE_FALLBACK)
            .bufferSource(ENTITY)
            .bufferSource(POS_TEX)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .mode(VertexFormat.Mode.LINES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();

    public static final RedirectingOutlineBufferSource OUTLINE = RedirectingOutlineBufferSource.builder()
            .fallback(OUTLINE_FALLBACK)
            .bufferSource(OUTLINE_MASK)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .mode(VertexFormat.Mode.LINES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();

    public static final SimpleOutlineBufferSource CORE_OUTLINE = new SimpleOutlineBufferSource(CORE, OUTLINE);

    public static int getPooledBufferSetSize() {
        return FeatureConfig.CONFIG.corePooledBufferSetSize.getAsInt();
    }

    public static int getPooledElementBufferSize() {
        return FeatureConfig.CONFIG.corePooledElementBufferSize.getAsInt();
    }

    public static boolean shouldUseVanillaBatching() {
        return FeatureConfig.CONFIG.coreUseVanillaBatching.get() == FeatureStatus.ENABLED;
    }
}

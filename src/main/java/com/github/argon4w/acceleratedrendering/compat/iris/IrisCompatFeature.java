package com.github.argon4w.acceleratedrendering.compat.iris;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.buffers.fallback.FallbackBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.fallback.VanillaBatchingBufferSource;
import net.minecraft.client.renderer.RenderBuffers;

public class IrisCompatFeature {

    public static final RenderBuffers SHADOW_VANILLA_RENDER_BUFFERS = new RenderBuffers(Runtime.getRuntime().availableProcessors());
    public static final VanillaBatchingBufferSource SHADOW_BATCHING = new VanillaBatchingBufferSource();
    public static final FallbackBufferSource SHADOW_FALLBACK = new FallbackBufferSource(SHADOW_VANILLA_RENDER_BUFFERS.bufferSource(), SHADOW_BATCHING);
    public static final AcceleratedBufferSource ENTITY_SHADOW = new AcceleratedBufferSource(IBufferEnvironment.Presets.getEntityEnvironment());

    public static final RedirectingBufferSource SHADOW = RedirectingBufferSource.builder()
            .fallback(SHADOW_FALLBACK)
            .bufferSource(ENTITY_SHADOW)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();

    public static boolean isEnabled() {
        return FeatureConfig.CONFIG.irisCompatFeatureStatus.get() == FeatureStatus.ENABLED;
    }

    public static boolean isIrisCompatCullingEnabled() {
        return FeatureConfig.CONFIG.irisCompatNormalCullingCompat.get() == FeatureStatus.ENABLED;
    }

    public static boolean isIrisCompatEntitiesEnabled() {
        return FeatureConfig.CONFIG.irisCompatEntitiesCompat.get() == FeatureStatus.ENABLED;
    }
}

package com.github.argon4w.acceleratedrendering.compat.iris;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.buffers.fallback.FallbackBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.fallback.VanillaBatchingBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingBufferSource;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderBuffers;

import java.util.ArrayDeque;
import java.util.Deque;

public class IrisCompatFeature {

    public static final Deque<FeatureStatus> SHADOW_CULLING_CONTROLLER_STACK = new ArrayDeque<>();
    public static final Deque<FeatureStatus> POLYGON_PROCESSING_CONTROLLER_STACK = new ArrayDeque<>();

    public static final RenderBuffers SHADOW_VANILLA_RENDER_BUFFERS = new RenderBuffers(Runtime.getRuntime().availableProcessors());
    public static final VanillaBatchingBufferSource SHADOW_BATCHING = new VanillaBatchingBufferSource();
    public static final FallbackBufferSource SHADOW_FALLBACK = new FallbackBufferSource(SHADOW_VANILLA_RENDER_BUFFERS.bufferSource(), SHADOW_BATCHING);
    public static final AcceleratedBufferSource ENTITY_SHADOW = new AcceleratedBufferSource(IBufferEnvironment.Presets.getEntityEnvironment());

    public static final RedirectingBufferSource SHADOW = RedirectingBufferSource.builder()
            .fallback(SHADOW_FALLBACK)
            .bufferSource(ENTITY_SHADOW)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .mode(VertexFormat.Mode.LINES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build();

    public static boolean isEnabled() {
        return FeatureConfig.CONFIG.irisCompatFeatureStatus.get() == FeatureStatus.ENABLED;
    }

    public static boolean isIrisCompatCullingEnabled() {
        return FeatureConfig.CONFIG.irisCompatNormalCullingCompat.get() == FeatureStatus.ENABLED;
    }

    public static boolean isShadowCullingEnabled() {
        return getShadowCullingSetting() == FeatureStatus.ENABLED;
    }

    public static boolean isPolygonProcessingEnabled() {
        return getPolygonProcessingSetting() == FeatureStatus.ENABLED;
    }

    public static boolean isIrisCompatEntitiesEnabled() {
        return FeatureConfig.CONFIG.irisCompatEntitiesCompat.get() == FeatureStatus.ENABLED;
    }

    public static void disableShadowCulling() {
        SHADOW_CULLING_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
    }

    public static void disablePolygonProcessing() {
        POLYGON_PROCESSING_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
    }

    public static void forceEnableShadowCulling() {
        SHADOW_CULLING_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
    }

    public static void enablePolygonProcessing() {
        POLYGON_PROCESSING_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
    }

    public static void forceSetShadowCulling(FeatureStatus status) {
        SHADOW_CULLING_CONTROLLER_STACK.push(status);
    }

    public static void forceSetIrisPolygonProcessing(FeatureStatus status) {
        POLYGON_PROCESSING_CONTROLLER_STACK.push(status);
    }

    public static void resetShadowCulling() {
        SHADOW_CULLING_CONTROLLER_STACK.pop();
    }

    public static void resetPolygonProcessing() {
        POLYGON_PROCESSING_CONTROLLER_STACK.pop();
    }

    public static FeatureStatus getShadowCullingSetting() {
        return SHADOW_CULLING_CONTROLLER_STACK.isEmpty() ? getDefaultShadowCullingSetting() : SHADOW_CULLING_CONTROLLER_STACK.peek();
    }

    public static FeatureStatus getPolygonProcessingSetting() {
        return POLYGON_PROCESSING_CONTROLLER_STACK.isEmpty() ? getDefaultPolygonProcessingSetting() : POLYGON_PROCESSING_CONTROLLER_STACK.peek();
    }

    public static FeatureStatus getDefaultShadowCullingSetting() {
        return FeatureConfig.CONFIG.irisCompatShadowCulling.get();
    }

    public static FeatureStatus getDefaultPolygonProcessingSetting() {
        return FeatureConfig.CONFIG.irisCompatPolygonProcessing.get();
    }

    public static void checkControllerState() {
        if (!SHADOW_CULLING_CONTROLLER_STACK.isEmpty()) {
            throw new IllegalStateException("Shadow Culling Controller stack not empty!");
        }

        if (!POLYGON_PROCESSING_CONTROLLER_STACK.isEmpty()) {
            throw new IllegalStateException("Polygon Processing Controller stack not empty!");
        }
    }
}

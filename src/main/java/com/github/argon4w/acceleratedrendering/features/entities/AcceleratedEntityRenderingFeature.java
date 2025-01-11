package com.github.argon4w.acceleratedrendering.features.entities;

import com.github.argon4w.acceleratedrendering.configs.AcceleratedRenderingConfig;
import com.github.argon4w.acceleratedrendering.configs.DefaultPipeline;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshType;

import java.util.ArrayDeque;
import java.util.Deque;

public class AcceleratedEntityRenderingFeature {

    private static final Deque<Boolean> CONTROLLER_STACK = new ArrayDeque<>();

    public static boolean isFeatureEnabled() {
        return AcceleratedRenderingConfig.CONFIG.acceleratedEntityRenderingFeatureStatus.get() == FeatureStatus.ENABLED;
    }

    public static DefaultPipeline getDefaultPipeline() {
        return AcceleratedRenderingConfig.CONFIG.acceleratedEntityRenderingDefaultPipeline.get();
    }

    public static MeshType getMeshType() {
        return AcceleratedRenderingConfig.CONFIG.acceleratedEntityRenderingMeshType.get();
    }

    public static IMesh.Builder getMeshBuilder() {
        return getMeshType().getBuilder();
    }

    public static boolean shouldUseAcceleratedPipeline() {
        return isFeatureEnabled() && (CONTROLLER_STACK.isEmpty() ? (getDefaultPipeline() == DefaultPipeline.ACCELERATED) : CONTROLLER_STACK.peek());
    }

    public static void temporarilyDisableFeature() {
        CONTROLLER_STACK.push(false);
    }

    public static void temporarilyForceEnableFeature() {
        CONTROLLER_STACK.push(true);
    }

    public static void resetFeatureTemporarySetting() {
        CONTROLLER_STACK.pop();
    }

    public static void checkControllerState() {
        if (!CONTROLLER_STACK.isEmpty()) {
            throw new IllegalStateException("Controller stack not empty!");
        }
    }
}

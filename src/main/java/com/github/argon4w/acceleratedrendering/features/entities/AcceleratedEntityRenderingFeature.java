package com.github.argon4w.acceleratedrendering.features.entities;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.DefaultPipeline;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshType;

import java.util.ArrayDeque;
import java.util.Deque;

public class AcceleratedEntityRenderingFeature {

    private static final Deque<DefaultPipeline> DEFAULT_PIPELINE_CONTROLLER_STACK = new ArrayDeque<>();

    public static boolean isEnabled() {
        return FeatureConfig.CONFIG.acceleratedEntityRenderingFeatureStatus.get() == FeatureStatus.ENABLED;
    }

    public static boolean shouldUseAcceleratedPipeline() {
        return DEFAULT_PIPELINE_CONTROLLER_STACK.isEmpty() ? (FeatureConfig.CONFIG.acceleratedEntityRenderingDefaultPipeline.get() == DefaultPipeline.ACCELERATED) : (DEFAULT_PIPELINE_CONTROLLER_STACK.peek() == DefaultPipeline.ACCELERATED);
    }

    public static MeshType getMeshType() {
        return FeatureConfig.CONFIG.acceleratedEntityRenderingMeshType.get();
    }

    public static IMesh.Builder getMeshBuilder() {
        return getMeshType().getBuilder();
    }

    public static void useVanillaPipeline() {
        DEFAULT_PIPELINE_CONTROLLER_STACK.push(DefaultPipeline.VANILLA);
    }

    public static void forceUseAcceleratedPipeline() {
        DEFAULT_PIPELINE_CONTROLLER_STACK.push(DefaultPipeline.ACCELERATED);
    }

    public static void forceSetPipeline(DefaultPipeline pipeline) {
        DEFAULT_PIPELINE_CONTROLLER_STACK.push(pipeline);
    }

    public static void resetPipelineSetting() {
        DEFAULT_PIPELINE_CONTROLLER_STACK.pop();
    }

    public static void checkControllerState() {
        if (!DEFAULT_PIPELINE_CONTROLLER_STACK.isEmpty()) {
            throw new IllegalStateException("Default pipeline Controller stack not empty!");
        }
    }
}

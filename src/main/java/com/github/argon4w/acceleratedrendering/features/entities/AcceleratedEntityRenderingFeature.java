package com.github.argon4w.acceleratedrendering.features.entities;

import com.github.argon4w.acceleratedrendering.configs.PipelineSetting;
import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshType;

import java.util.ArrayDeque;
import java.util.Deque;

public class AcceleratedEntityRenderingFeature {

    private static final Deque<PipelineSetting> DEFAULT_PIPELINE_CONTROLLER_STACK = new ArrayDeque<>();

    public static boolean isEnabled() {
        return FeatureConfig.CONFIG.acceleratedEntityRenderingFeatureStatus.get() == FeatureStatus.ENABLED;
    }

    public static PipelineSetting getDefaultPipeline() {
        return FeatureConfig.CONFIG.acceleratedEntityRenderingDefaultPipeline.get();
    }

    public static boolean shouldUseAcceleratedPipeline() {
        return getPipelineSetting() == PipelineSetting.ACCELERATED;
    }

    public static MeshType getMeshType() {
        return FeatureConfig.CONFIG.acceleratedEntityRenderingMeshType.get();
    }

    public static IMesh.Builder getMeshBuilder() {
        return getMeshType().getBuilder();
    }

    public static void useVanillaPipeline() {
        DEFAULT_PIPELINE_CONTROLLER_STACK.push(PipelineSetting.VANILLA);
    }

    public static void forceUseAcceleratedPipeline() {
        DEFAULT_PIPELINE_CONTROLLER_STACK.push(PipelineSetting.ACCELERATED);
    }

    public static void forceSetPipeline(PipelineSetting pipeline) {
        DEFAULT_PIPELINE_CONTROLLER_STACK.push(pipeline);
    }

    public static void resetPipelineSetting() {
        DEFAULT_PIPELINE_CONTROLLER_STACK.pop();
    }

    public static PipelineSetting getPipelineSetting() {
        return DEFAULT_PIPELINE_CONTROLLER_STACK.isEmpty() ? getDefaultPipeline() : DEFAULT_PIPELINE_CONTROLLER_STACK.peek();
    }

    public static void checkControllerState() {
        if (!DEFAULT_PIPELINE_CONTROLLER_STACK.isEmpty()) {
            throw new IllegalStateException("Default pipeline Controller stack not empty!");
        }
    }
}

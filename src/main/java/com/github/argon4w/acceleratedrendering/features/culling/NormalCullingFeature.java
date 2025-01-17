package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;

import java.util.ArrayDeque;
import java.util.Deque;

public class NormalCullingFeature {

    private static final Deque<Boolean> CULLING_CONTROLLER_STACK = new ArrayDeque<>();

    public static boolean isEnabled() {
        return FeatureConfig.CONFIG.normalCullingFeatureStatus.get() == FeatureStatus.ENABLED;
    }

    public static boolean shouldIgnoreCullState() {
        return FeatureConfig.CONFIG.normalCullingIgnoreCullState.get() == FeatureStatus.ENABLED;
    }

    public static boolean shouldCull() {
        return CULLING_CONTROLLER_STACK.isEmpty() || CULLING_CONTROLLER_STACK.peek();
    }

    public static void disableCulling() {
        CULLING_CONTROLLER_STACK.push(false);
    }

    public static void forceEnableCulling() {
        CULLING_CONTROLLER_STACK.push(true);
    }

    public static void forceSetCulling(boolean culling) {
        CULLING_CONTROLLER_STACK.push(culling);
    }

    public static void resetCullingSetting() {
        CULLING_CONTROLLER_STACK.pop();
    }

    public static void checkControllerState() {
        if (!CULLING_CONTROLLER_STACK.isEmpty()) {
            throw new IllegalStateException("Culling Controller stack not empty!");
        }
    }
}

package com.github.argon4w.acceleratedrendering.core;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;

import java.util.ArrayDeque;

public class CoreFeature {

    public static final ArrayDeque<FeatureStatus> FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK = new ArrayDeque<>();

    public static int getPooledBufferSetSize() {
        return FeatureConfig.CONFIG.corePooledBufferSetSize.getAsInt();
    }

    public static int getPooledElementBufferSize() {
        return FeatureConfig.CONFIG.corePooledElementBufferSize.getAsInt();
    }

    public static boolean shouldForceAccelerateTranslucent() {
        return getForceTranslucentAccelerationSetting() == FeatureStatus.ENABLED;
    }

    public static void disableForceTranslucentAcceleration() {
        FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
    }

    public static void forceEnableForceTranslucentAcceleration() {
        FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
    }

    public static void forceSetForceTranslucentAcceleration(FeatureStatus status) {
        FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.push(status);
    }

    public static void resetForceTranslucentAcceleration() {
        FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.pop();
    }

    public static FeatureStatus getForceTranslucentAccelerationSetting() {
        return FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.isEmpty() ? getDefaultForceTranslucentAccelerationSetting() : FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.peek();
    }

    public static FeatureStatus getDefaultForceTranslucentAccelerationSetting() {
        return FeatureConfig.CONFIG.coreForceTranslucentAcceleration.get();
    }

    public static void checkControllerState() {
        if (!FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.isEmpty()) {
            throw new IllegalStateException("Force Translucent Acceleration Controller stack not empty!");
        }
    }
}

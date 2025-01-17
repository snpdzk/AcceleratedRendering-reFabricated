package com.github.argon4w.acceleratedrendering.core.utils;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class RenderTypeUtils {

    public static Optional<ResourceLocation> getTextureLocation(RenderType renderType) {
        if (renderType == null) {
            return Optional.empty();
        }

        if (!(renderType instanceof RenderType.CompositeRenderType composite)) {
            return Optional.empty();
        }

        return composite.state.textureState.cutoutTexture();
    }

    public static boolean isCulled(RenderType renderType) {
        if (renderType == null) {
            return false;
        }

        if (!(renderType instanceof RenderType.CompositeRenderType composite)) {
            return false;
        }

        return composite.state.cullState.enabled;
    }
}

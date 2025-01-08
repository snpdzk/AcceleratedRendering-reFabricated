package com.github.argon4w.acceleratedrendering.compat;

import net.minecraft.client.renderer.RenderType;
import net.neoforged.fml.ModList;

public class IrisCompat {
    public static boolean isInstalled() {
        return ModList.get().isLoaded("iris");
    }

    public static RenderType unwrapRenderType(RenderType renderType) {
        return isInstalled() ? IrisCompatImpl.unwrapRenderType(renderType) : renderType;
    }
}

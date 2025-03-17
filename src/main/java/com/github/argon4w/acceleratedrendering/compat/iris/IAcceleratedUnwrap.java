package com.github.argon4w.acceleratedrendering.compat.iris;

import net.minecraft.client.renderer.RenderType;

public interface IAcceleratedUnwrap {

    default RenderType unwrapFast() {
        return (RenderType) this;
    }

    default boolean isAccelerated() {
        return false;
    }
}

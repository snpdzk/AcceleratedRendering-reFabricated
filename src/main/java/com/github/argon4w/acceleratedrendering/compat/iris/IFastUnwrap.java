package com.github.argon4w.acceleratedrendering.compat.iris;

import net.minecraft.client.renderer.RenderType;

public interface IFastUnwrap {

    default RenderType unwrapFast() {
        return (RenderType) this;
    }

    default boolean supportFastUnwrap() {
        return false;
    }
}

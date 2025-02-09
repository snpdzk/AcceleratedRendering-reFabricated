package com.github.argon4w.acceleratedrendering.compat.iris.buffers;

import net.minecraft.client.renderer.RenderType;

public interface IRenderTypeExtension {

    RenderType getOrUnwrap();
    boolean isFastUnwrapSupported();
}

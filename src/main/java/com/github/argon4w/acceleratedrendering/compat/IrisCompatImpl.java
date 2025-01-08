package com.github.argon4w.acceleratedrendering.compat;

import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.renderer.RenderType;

public class IrisCompatImpl {
    public static RenderType unwrapRenderType(RenderType renderType) {
        return renderType instanceof WrappableRenderType wrappable ? wrappable.unwrap() : renderType;
    }
}

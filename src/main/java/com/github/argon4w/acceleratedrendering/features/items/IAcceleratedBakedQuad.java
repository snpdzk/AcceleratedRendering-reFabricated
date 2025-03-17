package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IAcceleratedVertexConsumer;

public interface IAcceleratedBakedQuad {

    void renderFast(IAcceleratedVertexConsumer extension, int combinedLight, int combinedOverlay, int color);
    boolean hasCustomColor();
    int getCustomColor();
}

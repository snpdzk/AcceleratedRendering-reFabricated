package com.github.argon4w.acceleratedrendering.builders;

import com.github.argon4w.acceleratedrendering.buffers.IVertexConsumerExtension;

public interface IMesh {
    void render(IVertexConsumerExtension extension, int color, int light, int overlay);
}

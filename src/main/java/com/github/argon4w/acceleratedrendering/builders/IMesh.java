package com.github.argon4w.acceleratedrendering.builders;

public interface IMesh {
    void render(IVertexConsumerExtension extension, int color, int light, int overlay);
}

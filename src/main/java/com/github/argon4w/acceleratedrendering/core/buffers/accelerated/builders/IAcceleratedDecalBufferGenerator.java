package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.mojang.blaze3d.vertex.VertexConsumer;

public interface IAcceleratedDecalBufferGenerator {

    VertexConsumer generate(AcceleratedBufferBuilder delegate, int decal, int color);
}

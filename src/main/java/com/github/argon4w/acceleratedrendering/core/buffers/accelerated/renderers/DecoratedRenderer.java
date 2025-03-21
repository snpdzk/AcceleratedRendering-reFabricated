package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class DecoratedRenderer<T> implements IAcceleratedRenderer<T> {

    private final IAcceleratedRenderer<T> renderer;
    private final IBufferDecorator bufferDecorator;

    public DecoratedRenderer(IAcceleratedRenderer<T> renderer, IBufferDecorator bufferDecorator) {
        this.renderer = renderer;
        this.bufferDecorator = bufferDecorator;
    }

    @Override
    public void render(
            VertexConsumer vertexConsumer,
            T context,
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            int light,
            int overlay,
            int color
    ) {
        renderer.render(
                bufferDecorator.decorate(vertexConsumer),
                context,
                transformMatrix,
                normalMatrix,
                light,
                overlay,
                color
        );
    }
}

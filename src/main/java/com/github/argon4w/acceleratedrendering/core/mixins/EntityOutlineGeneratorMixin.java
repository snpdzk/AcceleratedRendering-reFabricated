package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.AcceleratedEntityOutlineGenerator;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.DecoratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IBufferDecorator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(OutlineBufferSource.EntityOutlineGenerator.class)
public class EntityOutlineGeneratorMixin implements IAcceleratedVertexConsumer, IBufferDecorator {

    @Shadow @Final private VertexConsumer delegate;
    @Shadow @Final private int color;

    @Unique
    @Override
    public VertexConsumer decorate(VertexConsumer buffer) {
        return new AcceleratedEntityOutlineGenerator(buffer, color);
    }

    @Unique
    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public void endTransform() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public boolean isAccelerated() {
        return ((IAcceleratedVertexConsumer) delegate).isAccelerated();
    }

    @Unique
    @Override
    public RenderType getRenderType() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public void addClientMesh(
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public void addServerMesh(
            int offset,
            int size,
            int color,
            int light,
            int overlay
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public VertexConsumer getDecal(
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            float scale,
            int color
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public <T>  void doRender(
            IAcceleratedRenderer<T> renderer,
            T context,
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            int light,
            int overlay,
            int color
    ) {
        ((IAcceleratedVertexConsumer) delegate).doRender(
                renderer.decorate(this),
                context,
                transformMatrix,
                normalMatrix,
                light,
                overlay,
                color
        );
    }
}

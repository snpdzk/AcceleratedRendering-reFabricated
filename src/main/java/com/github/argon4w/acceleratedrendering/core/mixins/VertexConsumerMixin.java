package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedDecalBufferGenerator;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends IAcceleratedVertexConsumer, VertexConsumer {

    @Unique
    @Override
    default void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default void endTransform() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default boolean isAccelerated() {
        return false;
    }

    @Unique
    @Override
    default RenderType getRenderType() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default TextureAtlasSprite getSprite() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default void addClientMesh(
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay,
            int decal
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default void addServerMesh(
            int offset,
            int size,
            int color,
            int light,
            int overlay,
            int decal
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default VertexConsumer getDecal(
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            float scale,
            int color,
            Vector2f uv0,
            Vector2f uv1,
            IAcceleratedDecalBufferGenerator generator
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default <T> void doRender(
            IAcceleratedRenderer<T> renderer,
            T context,
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            int light,
            int overlay,
            int color
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default VertexConsumer addVertex(
            float pX,
            float pY,
            float pZ,
            int decal
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ,
            int decal
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    default void addVertex(
            float pX,
            float pY,
            float pZ,
            int pColor,
            float pU,
            float pV,
            int pPackedOverlay,
            int pPackedLight,
            float pNormalX,
            float pNormalY,
            float pNormalZ,
            int decal
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }
}

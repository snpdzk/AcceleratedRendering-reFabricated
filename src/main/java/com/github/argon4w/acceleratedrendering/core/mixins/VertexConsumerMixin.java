package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends IAcceleratedVertexConsumer {

    @Unique
    @Override
    default void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {

    }

    @Unique
    @Override
    default void endTransform() {

    }

    @Unique
    @Override
    default boolean isAccelerated() {
        return false;
    }

    @Override
    default void mapRenderTypes(Map<RenderType, VertexConsumer> map) {

    }

    @Unique
    @Override
    default Set<RenderType> getRenderTypes() {
        return Set.of();
    }

    @Unique
    @Override
    default void addClientMesh(
            RenderType renderType,
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {

    }

    @Unique
    @Override
    default void addServerMesh(
            RenderType renderType,
            int offset,
            int size,
            int color,
            int light,
            int overlay
    ) {

    }
}

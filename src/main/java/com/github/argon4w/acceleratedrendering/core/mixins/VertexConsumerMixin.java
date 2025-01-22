package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IVertexConsumerExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;
import java.util.Set;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends IVertexConsumerExtension {

    @Unique
    @Override
    default void beginTransform(PoseStack.Pose pose) {

    }

    @Unique
    @Override
    default void endTransform() {

    }

    @Unique
    @Override
    default void addClientMesh(RenderType renderType, ByteBuffer vertexBuffer, int size, int color, int light, int overlay) {

    }

    @Unique
    @Override
    default void addServerMesh(RenderType renderType, int offset, int size, int color, int light, int overlay) {

    }

    @Unique
    @Override
    default boolean supportAcceleratedRendering() {
        return false;
    }

    @Unique
    @Override
    default Set<RenderType> getRenderTypes() {
        return Set.of();
    }
}

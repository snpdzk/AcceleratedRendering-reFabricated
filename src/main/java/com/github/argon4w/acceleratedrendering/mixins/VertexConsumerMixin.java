package com.github.argon4w.acceleratedrendering.mixins;

import com.github.argon4w.acceleratedrendering.builders.IVertexConsumerExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.ByteBuffer;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends IVertexConsumerExtension {

    @Override
    default void acceleratedrendering$beginTransform(PoseStack.Pose pose) {

    }

    @Override
    default void acceleratedrendering$addMesh(ByteBuffer vertexBuffer, int size, int color, int light, int overlay) {

    }

    @Override
    default boolean acceleratedrendering$supportAcceleratedRendering() {
        return false;
    }

    @Override
    default RenderType acceleratedrendering$getRenderType() {
        return null;
    }
}

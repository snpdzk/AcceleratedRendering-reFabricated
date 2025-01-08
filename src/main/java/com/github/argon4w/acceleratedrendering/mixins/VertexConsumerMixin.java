package com.github.argon4w.acceleratedrendering.mixins;

import com.github.argon4w.acceleratedrendering.buffers.IVertexConsumerExtension;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.ByteBuffer;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends IVertexConsumerExtension {

    @Override
    default MeshData sme$build() {
        return null;
    }

    @Override
    default void sme$beginTransform(PoseStack.Pose pose) {

    }

    @Override
    default void sme$addMesh(ByteBuffer vertexBuffer, int size, int color, int light, int overlay) {

    }

    @Override
    default boolean sme$supportAcceleratedRendering() {
        return false;
    }

    @Override
    default RenderType sme$getRenderType() {
        return null;
    }
}

package com.example.examplemod.mixins;

import com.example.examplemod.IBufferBuilderExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.ByteBuffer;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends IBufferBuilderExtension {
    @Override
    default void sme$setRenderType(RenderType renderType) {

    }

    @Override
    default void sme$beginTransform(PoseStack.Pose pose) {

    }

    @Override
    default void sme$setTransformIndex(int count) {

    }

    @Override
    default void sme$addMesh(ByteBuffer buffer, int size) {

    }
}

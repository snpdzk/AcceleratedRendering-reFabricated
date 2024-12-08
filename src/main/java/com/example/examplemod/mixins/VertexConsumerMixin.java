package com.example.examplemod.mixins;

import com.example.examplemod.IBufferBuilderExtension;
import com.example.examplemod.IEntityBufferSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.ByteBuffer;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends IBufferBuilderExtension {
    @Override
    default void sme$supply(IEntityBufferSet bufferSet, RenderType renderType) {

    }

    @Override
    default void sme$beginTransform(PoseStack.Pose pose) {

    }

    @Override
    default void sme$addMesh(ByteBuffer vertexBuffer, int size) {

    }

    @Override
    default boolean sme$supportAcceleratedRendering() {
        return false;
    }

    @Override
    default int sme$getVertices() {
        return 0;
    }

    @Override
    default RenderType sme$getRenderType() {
        return null;
    }
}

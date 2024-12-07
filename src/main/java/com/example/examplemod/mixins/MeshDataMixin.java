package com.example.examplemod.mixins;

import com.example.examplemod.IMeshDataExtension;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(MeshData.class)
public class MeshDataMixin implements IMeshDataExtension {
    @Unique private ByteBufferBuilder.Result sme$transformIndexBuffer;
    @Unique private ByteBufferBuilder.Result sme$transformBuffer;
    @Unique private RenderType sme$renderType;

    @Override
    public void sme$setTransformIndexBuffer(ByteBufferBuilder.Result result) {
        sme$transformIndexBuffer = result;
    }

    @Override
    public void sme$setTransformBuffer(ByteBufferBuilder.Result result) {
        sme$transformBuffer = result;
    }

    @Override
    public void sme$SetRenderType(RenderType type) {
        sme$renderType = type;
    }

    @Override
    public ByteBufferBuilder.Result sme$getTransformIndexBuffer() {
        return sme$transformIndexBuffer;
    }

    @Override
    public ByteBufferBuilder.Result sme$getTransformBuffer() {
        return sme$transformBuffer;
    }

    @Override
    public RenderType sme$getRenderType() {
        return sme$renderType;
    }
}

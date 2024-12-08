package com.example.examplemod;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.RenderType;

public interface IMeshDataExtension {
    void sme$setTransformIndexBuffer(ByteBufferBuilder.Result result);
    void sme$setTransformBuffer(ByteBufferBuilder.Result result);
    void sme$setNormalBuffer(ByteBufferBuilder.Result result);
    void sme$SetRenderType(RenderType type);

    ByteBufferBuilder.Result sme$getTransformIndexBuffer();
    ByteBufferBuilder.Result sme$getTransformBuffer();
    ByteBufferBuilder.Result sme$getNormalBuffer();
    RenderType sme$getRenderType();
}

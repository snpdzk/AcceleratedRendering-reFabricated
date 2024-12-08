package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;

public interface IBufferBuilderExtension {
    void sme$supply(IEntityBufferSet bufferSet, RenderType renderType);
    void sme$beginTransform(PoseStack.Pose pose);
    void sme$addMesh(ByteBuffer vertexBuffer, int size);
    int sme$getVertices();
    boolean sme$supportAcceleratedRendering();
    RenderType sme$getRenderType();
}

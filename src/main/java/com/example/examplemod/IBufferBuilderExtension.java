package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;

public interface IBufferBuilderExtension {
    void sme$setRenderType(RenderType renderType);
    void sme$beginTransform(PoseStack.Pose pose);
    void sme$setTransformIndex(int count);
    void sme$addMesh(ByteBuffer buffer, int size);
    int sme$getVertices();
}

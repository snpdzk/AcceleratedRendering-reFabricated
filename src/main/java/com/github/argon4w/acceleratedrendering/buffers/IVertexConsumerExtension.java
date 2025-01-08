package com.github.argon4w.acceleratedrendering.buffers;

import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;

public interface IVertexConsumerExtension {
    MeshData sme$build();
    void sme$beginTransform(PoseStack.Pose pose);
    void sme$addMesh(ByteBuffer vertexBuffer, int size, int color, int light, int overlay);
    boolean sme$supportAcceleratedRendering();
    RenderType sme$getRenderType();
}

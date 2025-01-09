package com.github.argon4w.acceleratedrendering.builders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;

public interface IVertexConsumerExtension {
    void acceleratedrendering$beginTransform(PoseStack.Pose pose);
    void acceleratedrendering$addMesh(ByteBuffer vertexBuffer, int size, int color, int light, int overlay);
    boolean acceleratedrendering$supportAcceleratedRendering();
    RenderType acceleratedrendering$getRenderType();
}

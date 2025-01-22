package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;
import java.util.Set;

public interface IVertexConsumerExtension {

    void beginTransform(PoseStack.Pose pose);
    void endTransform();
    void addClientMesh(RenderType renderType, ByteBuffer vertexBuffer, int size, int color, int light, int overlay);
    void addServerMesh(RenderType renderType, int offset, int size, int color, int light, int overlay);
    boolean supportAcceleratedRendering();
    Set<RenderType> getRenderTypes();
}

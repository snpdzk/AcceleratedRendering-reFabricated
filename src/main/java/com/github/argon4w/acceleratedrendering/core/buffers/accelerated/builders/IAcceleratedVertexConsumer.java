package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

public interface IAcceleratedVertexConsumer {

    void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix);
    void endTransform();
    boolean isAccelerated();
    void mapRenderTypes(Map<RenderType, VertexConsumer> map);
    Set<RenderType> getRenderTypes();
    void addClientMesh(RenderType renderType, ByteBuffer meshBuffer, int size, int color, int light, int overlay);
    void addServerMesh(RenderType renderType, int offset, int size, int color, int light, int overlay);
}

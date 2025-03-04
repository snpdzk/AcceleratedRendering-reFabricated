package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.Set;

public interface IAcceleratedVertexConsumer {

    void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix);
    void endTransform();
    void addClientMesh(RenderType renderType, ByteBuffer vertexBuffer, int size, int color, int light, int overlay);
    void addServerMesh(RenderType renderType, int offset, int size, int color, int light, int overlay);
    boolean isAccelerated();
    Set<RenderType> getRenderTypes();
}

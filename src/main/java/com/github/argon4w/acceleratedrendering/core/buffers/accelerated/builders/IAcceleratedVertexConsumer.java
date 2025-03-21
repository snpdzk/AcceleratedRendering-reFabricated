package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

public interface IAcceleratedVertexConsumer {

    void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix);
    void endTransform();
    boolean isAccelerated();
    RenderType getRenderType();
    <T> void doRender(IAcceleratedRenderer<T> renderer, T context, Matrix4f transformMatrix, Matrix3f normalMatrix, int light, int overlay, int color);
    void addClientMesh(ByteBuffer meshBuffer, int size, int color, int light, int overlay);
    void addServerMesh(int offset, int size, int color, int light, int overlay);
    VertexConsumer getDecal(Matrix4f transformMatrix, Matrix3f normalMatrix, float scale, int color);
}

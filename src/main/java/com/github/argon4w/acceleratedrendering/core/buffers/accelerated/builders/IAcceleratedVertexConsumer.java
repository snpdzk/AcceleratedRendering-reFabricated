package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.nio.ByteBuffer;

public interface IAcceleratedVertexConsumer {

    void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix);
    void endTransform();
    boolean isAccelerated();
    RenderType getRenderType();
    <T> void doRender(IAcceleratedRenderer<T> renderer, T context, Matrix4f transformMatrix, Matrix3f normalMatrix, int light, int overlay, int color);
    void addClientMesh(ByteBuffer meshBuffer, int size, int color, int light, int overlay, int decal);
    void addServerMesh(int offset, int size, int color, int light, int overlay, int decal);
    VertexConsumer getDecal(Matrix4f transformMatrix, Matrix3f normalMatrix, float scale, int color, Vector2f uv0, Vector2f uv1, IAcceleratedDecalBufferGenerator generator);
    VertexConsumer addVertex(float pX, float pY, float pZ, int decal);
    VertexConsumer addVertex(PoseStack.Pose pPose, float pX, float pY, float pZ, int decal);
    void addVertex(float pX, float pY, float pZ, int pColor, float pU, float pV, int pPackedOverlay, int pPackedLight, float pNormalX, float pNormalY, float pNormalZ, int decal);
}

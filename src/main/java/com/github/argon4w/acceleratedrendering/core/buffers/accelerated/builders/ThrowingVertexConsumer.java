package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

public class ThrowingVertexConsumer implements VertexConsumer, IAcceleratedVertexConsumer {

    public static final ThrowingVertexConsumer INSTANCE = new ThrowingVertexConsumer();

    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public void endTransform() {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public boolean isAccelerated() {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public void mapRenderTypes(Map<RenderType, VertexConsumer> map) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public Set<RenderType> getRenderTypes() {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public void addClientMesh(
            RenderType renderType,
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public void addServerMesh(
            RenderType renderType,
            int offset,
            int size,
            int color,
            int light,
            int overlay
    ) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public VertexConsumer addVertex(
            float pX,
            float pY,
            float pZ
    ) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public VertexConsumer setColor(
            int pRed,
            int pGreen,
            int pBlue,
            int pAlpha
    ) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }

    @Override
    public VertexConsumer setNormal(
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        throw new IllegalStateException("No valid VertexConsumer found.");
    }
}

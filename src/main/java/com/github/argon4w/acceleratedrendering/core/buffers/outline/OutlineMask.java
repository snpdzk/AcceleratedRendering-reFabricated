package com.github.argon4w.acceleratedrendering.core.buffers.outline;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IAcceleratedVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.Set;

public class OutlineMask implements VertexConsumer, IAcceleratedVertexConsumer {

    private final VertexConsumer vertexConsumer;
    private final int teamColor;

    public OutlineMask(VertexConsumer vertexConsumer, int teamColor) {
        this.vertexConsumer = vertexConsumer;
        this.teamColor = teamColor;
    }

    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        ((IAcceleratedVertexConsumer) vertexConsumer).beginTransform(transformMatrix, normalMatrix);
    }

    @Override
    public void endTransform() {
        ((IAcceleratedVertexConsumer) vertexConsumer).endTransform();
    }

    @Override
    public void addClientMesh(
            RenderType renderType,
            ByteBuffer vertexBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        ((IAcceleratedVertexConsumer) vertexConsumer).addClientMesh(
                renderType,
                vertexBuffer,
                size,
                teamColor,
                -1,
                -1
        );
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
        ((IAcceleratedVertexConsumer) vertexConsumer).addServerMesh(
                renderType,
                offset,
                size,
                teamColor,
                -1,
                -1
        );
    }

    @Override
    public boolean isAccelerated() {
        return ((IAcceleratedVertexConsumer) vertexConsumer).isAccelerated();
    }

    @Override
    public Set<RenderType> getRenderTypes() {
        return ((IAcceleratedVertexConsumer) vertexConsumer).getRenderTypes();
    }

    @Override
    public VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ
    ) {
        return vertexConsumer
                .addVertex(
                        pPose,
                        pX,
                        pY,
                        pZ
                )
                .setColor(teamColor);
    }

    @Override
    public VertexConsumer addVertex(
            float pX,
            float pY,
            float pZ
    ) {
        vertexConsumer
                .addVertex(
                        pX,
                        pY,
                        pZ
                )
                .setColor(teamColor);

        return this;
    }

    @Override
    public VertexConsumer setColor(
            int pRed,
            int pGreen,
            int pBlue,
            int pAlpha
    ) {
        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        vertexConsumer.setUv(pU, pV);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        return this;
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        return this;
    }

    @Override
    public VertexConsumer setNormal(
            PoseStack.Pose pPose,
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        return this;
    }

    @Override
    public VertexConsumer setNormal(
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        return this;
    }

    @Override
    public void addVertex(
            float pX,
            float pY,
            float pZ,
            int pColor,
            float pU,
            float pV,
            int pPackedOverlay,
            int pPackedLight,
            float pNormalX,
            float pNormalY,
            float pNormalZ) {
        vertexConsumer
                .addVertex(
                        pX,
                        pY,
                        pZ
                )
                .setColor(teamColor)
                .setUv(pU, pV);
    }
}

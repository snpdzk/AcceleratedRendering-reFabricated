package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.Set;

public class AcceleratedDoubleVertexConsumer implements VertexConsumer, IAcceleratedVertexConsumer {

    private final VertexConsumer vertexConsumer1;
    private final VertexConsumer vertexConsumer2;
    private final Set<RenderType> renderTypes1;
    private final Set<RenderType> renderTypes2;
    private final Set<RenderType> renderTypes;

    public AcceleratedDoubleVertexConsumer(VertexConsumer vertexConsumer1, VertexConsumer vertexConsumer2) {
        this.vertexConsumer1 = vertexConsumer1;
        this.vertexConsumer2 = vertexConsumer2;

        this.renderTypes1 = ((IAcceleratedVertexConsumer) this.vertexConsumer1).getRenderTypes();
        this.renderTypes2 = ((IAcceleratedVertexConsumer) this.vertexConsumer2).getRenderTypes();

        this.renderTypes = new ObjectOpenHashSet<>();
        this.renderTypes.addAll(renderTypes1);
        this.renderTypes.addAll(renderTypes2);
    }

    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        ((IAcceleratedVertexConsumer) vertexConsumer1).beginTransform(transformMatrix, normalMatrix);
        ((IAcceleratedVertexConsumer) vertexConsumer2).beginTransform(transformMatrix, normalMatrix);
    }

    @Override
    public void endTransform() {
        ((IAcceleratedVertexConsumer) vertexConsumer1).endTransform();
        ((IAcceleratedVertexConsumer) vertexConsumer2).endTransform();
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
        if (renderTypes1.contains(renderType)) {
            ((IAcceleratedVertexConsumer) vertexConsumer1).addClientMesh(
                    renderType,
                    meshBuffer,
                    size,
                    color,
                    light,
                    overlay
            );
        } else if (renderTypes2.contains(renderType)) {
            ((IAcceleratedVertexConsumer) vertexConsumer2).addClientMesh(
                    renderType,
                    meshBuffer,
                    size,
                    color,
                    light,
                    overlay
            );
        } else {
            throw new IllegalArgumentException("Incorrect RenderType: " + renderType.toString());
        }
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
        if (renderTypes1.contains(renderType)) {
            ((IAcceleratedVertexConsumer) vertexConsumer1).addServerMesh(
                    renderType,
                    offset,
                    size,
                    color,
                    light,
                    overlay
            );
        } else if (renderTypes2.contains(renderType)) {
            ((IAcceleratedVertexConsumer) vertexConsumer2).addServerMesh(
                    renderType,
                    offset,
                    size,
                    color,
                    light,
                    overlay
            );
        } else {
            throw new IllegalArgumentException("Incorrect RenderType: " + renderType.toString());
        }
    }

    @Override
    public boolean isAccelerated() {
        return ((IAcceleratedVertexConsumer) vertexConsumer1).isAccelerated()
                && ((IAcceleratedVertexConsumer) vertexConsumer2).isAccelerated();
    }

    @Override
    public Set<RenderType> getRenderTypes() {
        return renderTypes;
    }

    @Override
    public VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ
    ) {
        vertexConsumer1.addVertex(
                pPose,
                pX,
                pY,
                pZ
        );
        vertexConsumer2.addVertex(
                pPose,
                pX,
                pY,
                pZ
        );

        return this;
    }

    @Override
    public VertexConsumer addVertex(
            float pX,
            float pY,
            float pZ
    ) {
        vertexConsumer1.addVertex(
                pX,
                pY,
                pZ
        );
        vertexConsumer2.addVertex(
                pX,
                pY,
                pZ
        );

        return this;
    }

    @Override
    public VertexConsumer setColor(
            int pRed,
            int pGreen,
            int pBlue,
            int pAlpha
    ) {
        vertexConsumer1.setColor(
                pRed,
                pGreen,
                pBlue,
                pAlpha
        );
        vertexConsumer2.setColor(
                pRed,
                pGreen,
                pBlue,
                pAlpha
        );

        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        vertexConsumer1.setUv(pU, pV);
        vertexConsumer2.setUv(pU, pV);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        vertexConsumer1.setUv1(pU, pV);
        vertexConsumer2.setUv1(pU, pV);

        return this;
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        vertexConsumer1.setUv2(pU, pV);
        vertexConsumer2.setUv2(pU, pV);

        return this;
    }

    @Override
    public VertexConsumer setNormal(
            PoseStack.Pose pPose,
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        vertexConsumer1.setNormal(
                pPose,
                pNormalX,
                pNormalY,
                pNormalZ
        );
        vertexConsumer2.setNormal(
                pPose,
                pNormalX,
                pNormalY,
                pNormalZ
        );

        return this;
    }

    @Override
    public VertexConsumer setNormal(
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        vertexConsumer1.setNormal(
                pNormalX,
                pNormalY,
                pNormalZ
        );
        vertexConsumer2.setNormal(
                pNormalX,
                pNormalY,
                pNormalZ
        );

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
        vertexConsumer1.addVertex(
                pX,
                pY,
                pZ,
                pColor,
                pU,
                pV,
                pPackedOverlay,
                pPackedLight,
                pNormalX,
                pNormalY,
                pNormalZ
        );
        vertexConsumer2.addVertex(
                pX,
                pY,
                pZ,
                pColor,
                pU,
                pV,
                pPackedOverlay,
                pPackedLight,
                pNormalX,
                pNormalY,
                pNormalZ
        );
    }
}

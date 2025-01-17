package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;
import java.util.Set;

public class AcceleratedDoubleVertexConsumer implements VertexConsumer, IVertexConsumerExtension {

    private final IBufferEnvironment bufferEnvironment;;

    private final VertexConsumer vertexConsumer1;
    private final VertexConsumer vertexConsumer2;

    private final RenderType renderType1;
    private final RenderType renderType2;

    public AcceleratedDoubleVertexConsumer(
            IBufferEnvironment bufferEnvironment,
            RenderType renderType1,
            VertexConsumer vertexConsumer1,
            RenderType renderType2,
            VertexConsumer vertexConsumer2
    ) {
        this.bufferEnvironment = bufferEnvironment;

        this.vertexConsumer1 = vertexConsumer1;
        this.vertexConsumer2 = vertexConsumer2;

        this.renderType1 = renderType1;
        this.renderType2 = renderType2;
    }

    @Override
    public void beginTransform(PoseStack.Pose pose) {
        ((IVertexConsumerExtension) vertexConsumer1).beginTransform(pose);
        ((IVertexConsumerExtension) vertexConsumer2).beginTransform(pose);
    }

    @Override
    public void endTransform() {
        ((IVertexConsumerExtension) vertexConsumer1).endTransform();
        ((IVertexConsumerExtension) vertexConsumer2).endTransform();
    }

    @Override
    public void addClientMesh(RenderType renderType, ByteBuffer vertexBuffer, int size, int color, int light, int overlay) {
        if (renderType1.equals(renderType)) {
            ((IVertexConsumerExtension) vertexConsumer1).addClientMesh(renderType, vertexBuffer, size, color, light, overlay);
        } else if (renderType2.equals(renderType)) {
            ((IVertexConsumerExtension) vertexConsumer2).addClientMesh(renderType, vertexBuffer, size, color, light, overlay);
        } else {
            throw new IllegalArgumentException("Incorrect RenderType: " + renderType.toString());
        }
    }

    @Override
    public void addServerMesh(RenderType renderType, int offset, int size, int color, int light, int overlay) {
        if (renderType1.equals(renderType)) {
            ((IVertexConsumerExtension) vertexConsumer1).addServerMesh(renderType, offset, size, color, light, overlay);
        } else if (renderType2.equals(renderType)) {
            ((IVertexConsumerExtension) vertexConsumer2).addServerMesh(renderType, offset, size, color, light, overlay);
        } else {
            throw new IllegalArgumentException("Incorrect RenderType: " + renderType.toString());
        }
    }

    @Override
    public boolean supportAcceleratedRendering() {
        return true;
    }

    @Override
    public Set<RenderType> getRenderTypes() {
        return Set.of(renderType1, renderType2);
    }

    @Override
    public IBufferEnvironment getBufferEnvironment() {
        return bufferEnvironment;
    }

    @Override
    public VertexConsumer addVertex(float pX, float pY, float pZ) {
        vertexConsumer1.addVertex(pX, pY, pZ);
        vertexConsumer2.addVertex(pX, pY, pZ);

        return this;
    }

    @Override
    public VertexConsumer setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
        vertexConsumer1.setColor(pRed, pGreen, pBlue, pAlpha);
        vertexConsumer2.setColor(pRed, pGreen, pBlue, pAlpha);

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
    public VertexConsumer setNormal(float pNormalX, float pNormalY, float pNormalZ) {
        vertexConsumer1.setNormal(pNormalX, pNormalY, pNormalZ);
        vertexConsumer2.setNormal(pNormalX, pNormalY, pNormalZ);

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
        vertexConsumer1.addVertex(pX, pY, pZ);
        vertexConsumer2.addVertex(pX, pY, pZ);
    }
}

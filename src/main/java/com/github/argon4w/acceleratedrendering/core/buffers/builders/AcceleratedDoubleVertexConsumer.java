package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;
import java.util.Set;

public class AcceleratedDoubleVertexConsumer implements VertexConsumer, IVertexConsumerExtension {

    private final VertexConsumer vertexConsumer1;
    private final VertexConsumer vertexConsumer2;
    private final Set<RenderType> renderTypes1;
    private final Set<RenderType> renderTypes2;
    private final Set<RenderType> renderTypes;

    public AcceleratedDoubleVertexConsumer(
            VertexConsumer vertexConsumer1,
            VertexConsumer vertexConsumer2
    ) {
        this.vertexConsumer1 = vertexConsumer1;
        this.vertexConsumer2 = vertexConsumer2;

        this.renderTypes1 = ((IVertexConsumerExtension) this.vertexConsumer1).getRenderTypes();
        this.renderTypes2 = ((IVertexConsumerExtension) this.vertexConsumer2).getRenderTypes();

        this.renderTypes = new ObjectOpenHashSet<>();
        this.renderTypes.addAll(renderTypes1);
        this.renderTypes.addAll(renderTypes2);
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
    public void addClientMesh(
            RenderType renderType,
            ByteBuffer vertexBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        if (renderTypes1.contains(renderType)) {
            ((IVertexConsumerExtension) vertexConsumer1).addClientMesh(
                    renderType,
                    vertexBuffer,
                    size,
                    color,
                    light,
                    overlay
            );
        } else if (renderTypes2.contains(renderType)) {
            ((IVertexConsumerExtension) vertexConsumer2).addClientMesh(
                    renderType,
                    vertexBuffer,
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
            ((IVertexConsumerExtension) vertexConsumer1).addServerMesh(
                    renderType,
                    offset,
                    size,
                    color,
                    light,
                    overlay
            );
        } else if (renderTypes2.contains(renderType)) {
            ((IVertexConsumerExtension) vertexConsumer2).addServerMesh(
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
    public boolean supportAcceleratedRendering() {
        return ((IVertexConsumerExtension) vertexConsumer1).supportAcceleratedRendering()
                && ((IVertexConsumerExtension) vertexConsumer2).supportAcceleratedRendering();
    }

    @Override
    public Set<RenderType> getRenderTypes() {
        return renderTypes;
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

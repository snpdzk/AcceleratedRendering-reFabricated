package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.nio.ByteBuffer;

public class AcceleratedEntityOutlineGenerator implements IAcceleratedVertexConsumer, VertexConsumer {

    private final VertexConsumer delegate;
    private final int color;

    public AcceleratedEntityOutlineGenerator(VertexConsumer delegate, int color) {
        this.delegate = delegate;
        this.color = color;
    }

    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        ((IAcceleratedVertexConsumer) delegate).beginTransform(transformMatrix, normalMatrix);
    }

    @Override
    public void endTransform() {
        ((IAcceleratedVertexConsumer) delegate).endTransform();
    }

    @Override
    public boolean isAccelerated() {
        return ((IAcceleratedVertexConsumer) delegate).isAccelerated();
    }

    @Override
    public RenderType getRenderType() {
        return ((IAcceleratedVertexConsumer) delegate).getRenderType();
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return ((IAcceleratedVertexConsumer) delegate).getSprite();
    }

    @Override
    public void addClientMesh(
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay,
            int decal
    ) {
        ((IAcceleratedVertexConsumer) delegate).addClientMesh(
                meshBuffer,
                size,
                this.color,
                light,
                overlay,
                decal
        );
    }

    @Override
    public void addServerMesh(
            int offset,
            int size,
            int color,
            int light,
            int overlay,
            int decal
    ) {
        ((IAcceleratedVertexConsumer) delegate).addServerMesh(
                offset,
                size,
                this.color,
                light,
                overlay,
                decal
        );
    }

    @Override
    public VertexConsumer getDecal(
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            float scale,
            int color,
            Vector2f uv0,
            Vector2f uv1,
            IAcceleratedDecalBufferGenerator generator
    ) {
        return new AcceleratedEntityOutlineGenerator(((IAcceleratedVertexConsumer) delegate).getDecal(
                transformMatrix,
                normalMatrix,
                scale,
                color,
                uv0,
                uv1,
                generator
        ), this.color);
    }

    @Override
    public <T>  void doRender(
            IAcceleratedRenderer<T> renderer,
            T context,
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            int light,
            int overlay,
            int color
    ) {
        renderer.render(
                this,
                context,
                transformMatrix,
                normalMatrix,
                light,
                overlay,
                color
        );
    }

    @Override
    public VertexConsumer addVertex(
            float pX,
            float pY,
            float pZ,
            int decal
    ) {
        ((IAcceleratedVertexConsumer) delegate).addVertex(
                pX,
                pY,
                pZ,
                decal
        ).setColor(color);
        return this;
    }

    @Override
    public VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ,
            int decal
    ) {
        ((IAcceleratedVertexConsumer) delegate).addVertex(
                pPose,
                pX,
                pY,
                pZ,
                decal
        ).setColor(color);
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
            float pNormalZ,
            int decal
    ) {
        ((IAcceleratedVertexConsumer) delegate).addVertex(
                pX,
                pY,
                pZ,
                color,
                0.0f,
                0.0f,
                -1,
                -1,
                0.0f,
                0.0f,
                0.0f,
                decal
        );
    }

    @Override
    public VertexConsumer addVertex(
            float pX,
            float pY,
            float pZ
    ) {
        delegate.addVertex(
                pX,
                pY,
                pZ
        ).setColor(color);
        return this;
    }

    @Override
    public VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ
    ) {
        delegate.addVertex(
                pPose,
                pX,
                pY,
                pZ
        ).setColor(color);
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
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
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
            float pNormalZ
    ) {
        delegate.addVertex(
                pX,
                pY,
                pZ,
                color,
                0.0f,
                0.0f,
                -1,
                -1,
                0.0f,
                0.0f,
                0.0f
        );
    }
}

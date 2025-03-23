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

public class AcceleratedSheetedDecalTextureGenerator implements IAcceleratedVertexConsumer, VertexConsumer {

    private static final Vector2f UV0 = new Vector2f(0.0f, 0.0f);
    private static final Vector2f UV1 = new Vector2f(1.0f, 1.0f);

    private final AcceleratedBufferBuilder delegate;
    private final int decal;
    private final int color;

    public AcceleratedSheetedDecalTextureGenerator(
            AcceleratedBufferBuilder delegate,
            int decal,
            int color
    ) {
        this.delegate = delegate;
        this.decal = decal;
        this.color = color;
    }

    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        delegate.beginTransform(transformMatrix, normalMatrix);
    }

    @Override
    public void endTransform() {
        delegate.endTransform();
    }

    @Override
    public boolean isAccelerated() {
        return delegate.isAccelerated();
    }

    @Override
    public RenderType getRenderType() {
        return delegate.getRenderType();
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return delegate.getSprite();
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
        delegate.addClientMesh(
                meshBuffer,
                size,
                this.color,
                light,
                overlay,
                this.decal
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
        delegate.addServerMesh(
                offset,
                size,
                this.color,
                light,
                overlay,
                this.decal
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
        return delegate.getDecal(
                transformMatrix,
                normalMatrix,
                scale,
                color,
                UV0,
                UV1,
                generator
        );
    }

    @Override
    public <T> void doRender(
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
        delegate.addVertex(
                pX,
                pY,
                pZ,
                decal
        );
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
        delegate.addVertex(
                pPose,
                pX,
                pY,
                pZ,
                decal
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
            float pNormalZ,
            int decal
    ) {
        delegate.addVertex(
                pX,
                pY,
                pZ,
                pColor,
                -1,
                -1,
                pPackedOverlay,
                pPackedLight,
                pNormalX,
                pNormalY,
                pNormalZ,
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
                pZ,
                decal
        );
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
                pZ,
                decal
        );
        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        return this;
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        delegate.setUv1(pU, pV);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        delegate.setUv2(pU, pV);
        return this;
    }

    @Override
    public VertexConsumer setColor(
            int pRed,
            int pGreen,
            int pBlue,
            int pAlpha
    ) {
        delegate.setColor(color);
        return this;
    }

    @Override
    public VertexConsumer setNormal(
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        delegate.setNormal(
                pNormalX,
                pNormalY,
                pNormalZ
        );
        return this;
    }

    @Override
    public VertexConsumer setNormal(
            PoseStack.Pose pPose,
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        delegate.setNormal(
                pPose,
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
            float pNormalZ
    ) {
        delegate.addVertex(
                pX,
                pY,
                pZ,
                this.color,
                -1,
                -1,
                pPackedOverlay,
                pPackedLight,
                pNormalX,
                pNormalY,
                pNormalZ,
                decal
        );
    }
}

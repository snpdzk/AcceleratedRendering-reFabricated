package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.utils.IUVMapper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.nio.ByteBuffer;

public class AcceleratedSpriteCoordinateExpander implements IAcceleratedVertexConsumer, VertexConsumer, IUVMapper {

    private final VertexConsumer delegate;
    private final TextureAtlasSprite sprite;

    public AcceleratedSpriteCoordinateExpander(VertexConsumer delegate, TextureAtlasSprite sprite) {
        this.delegate = delegate;
        this.sprite = sprite;
    }

    @Override
    public float mapU(float u) {
        return sprite.getU(u);
    }

    @Override
    public float mapV(float v) {
        return sprite.getV(v);
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
        return sprite;
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
                color,
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
                color,
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
        return ((IAcceleratedVertexConsumer) delegate).getDecal(
                transformMatrix,
                normalMatrix,
                scale,
                color,
                new Vector2f(this.sprite.getU(uv0.x), this.sprite.getV(uv0.y)),
                new Vector2f(this.sprite.getU(uv1.x), this.sprite.getV(uv1.y)),
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
        ((IAcceleratedVertexConsumer) delegate).addVertex(
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
        ((IAcceleratedVertexConsumer) delegate).addVertex(
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
        ((IAcceleratedVertexConsumer) delegate).addVertex(
                pX,
                pY,
                pZ,
                pColor,
                sprite.getU(pU),
                sprite.getV(pV),
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
                pZ
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
                pZ
        );
        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        delegate.setUv(sprite.getU(pU), sprite.getV(pV));
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
        delegate.setColor(
                pRed,
                pGreen,
                pBlue,
                pAlpha
        );
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
                pColor,
                sprite.getU(pU),
                sprite.getV(pV),
                pPackedOverlay,
                pPackedLight,
                pNormalX,
                pNormalY,
                pNormalZ
        );
    }
}

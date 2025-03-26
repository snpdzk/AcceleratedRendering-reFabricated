package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.IBufferGraph;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.SpriteBufferGraph;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

public class AcceleratedSpriteCoordinateExpander implements IAcceleratedVertexConsumer, VertexConsumer {

    private final VertexConsumer delegate;
    private final TextureAtlasSprite sprite;

    public AcceleratedSpriteCoordinateExpander(VertexConsumer delegate, TextureAtlasSprite sprite) {
        this.delegate = delegate;
        this.sprite = sprite;
    }

    @Override
    public VertexConsumer decorate(VertexConsumer buffer) {
        return new AcceleratedSpriteCoordinateExpander(((IAcceleratedVertexConsumer) delegate).decorate(buffer), sprite);
    }

    @Override
    public void beginTransform(Matrix4f transform, Matrix3f normal) {
        ((IAcceleratedVertexConsumer) delegate).beginTransform(transform, normal);
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
    public IBufferGraph getBufferGraph() {
        return new SpriteBufferGraph(((IAcceleratedVertexConsumer) delegate).getBufferGraph(), sprite);
    }

    @Override
    public RenderType getRenderType() {
        return ((IAcceleratedVertexConsumer) delegate).getRenderType();
    }

    @Override
    public void addClientMesh(
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        ((IAcceleratedVertexConsumer) delegate).addClientMesh(
                meshBuffer,
                size,
                color,
                light,
                overlay
        );
    }

    @Override
    public void addServerMesh(
            int offset,
            int size,
            int color,
            int light,
            int overlay
    ) {
        ((IAcceleratedVertexConsumer) delegate).addServerMesh(
                offset,
                size,
                color,
                light,
                overlay
        );
    }

    @Override
    public <T> void doRender(
            IAcceleratedRenderer<T> renderer,
            T context,
            Matrix4f transform,
            Matrix3f normal,
            int light,
            int overlay,
            int color
    ) {
        renderer.render(
                this,
                context,
                transform,
                normal,
                light,
                overlay,
                color
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

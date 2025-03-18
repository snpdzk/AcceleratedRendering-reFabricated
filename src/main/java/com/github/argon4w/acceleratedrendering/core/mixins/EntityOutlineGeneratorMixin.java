package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

@Mixin(OutlineBufferSource.EntityOutlineGenerator.class)
public abstract class EntityOutlineGeneratorMixin implements IAcceleratedVertexConsumer, VertexConsumer {

    @Shadow @Final private VertexConsumer delegate;
    @Shadow @Final private int color;

    @Unique
    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        ((IAcceleratedVertexConsumer) delegate).beginTransform(transformMatrix, normalMatrix);
    }

    @Unique
    @Override
    public void endTransform() {
        ((IAcceleratedVertexConsumer) delegate).endTransform();
    }

    @Unique
    @Override
    public boolean isAccelerated() {
        return ((IAcceleratedVertexConsumer) delegate).isAccelerated();
    }

    @Unique
    @Override
    public void mapRenderTypes(Map<RenderType, VertexConsumer> map) {
        ((IAcceleratedVertexConsumer) delegate).mapRenderTypes(map);
    }

    @Unique
    @Override
    public Set<RenderType> getRenderTypes() {
        return ((IAcceleratedVertexConsumer) delegate).getRenderTypes();
    }

    @Unique
    @Override
    public void addClientMesh(
            RenderType renderType,
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        ((IAcceleratedVertexConsumer) delegate).addClientMesh(
                renderType,
                meshBuffer,
                size,
                this.color,
                -1,
                -1
        );
    }

    @Unique
    @Override
    public void addServerMesh(
            RenderType renderType,
            int offset,
            int size,
            int color,
            int light,
            int overlay
    ) {
        ((IAcceleratedVertexConsumer) delegate).addServerMesh(
                renderType,
                offset,
                size,
                this.color,
                -1,
                -1
        );
    }

    @Override
    public VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ
    ) {
        return delegate
                .addVertex(
                        pPose,
                        pX,
                        pY,
                        pZ
                )
                .setColor(color);
    }
}

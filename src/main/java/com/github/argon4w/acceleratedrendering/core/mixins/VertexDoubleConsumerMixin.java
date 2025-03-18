package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.ThrowingVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(VertexMultiConsumer.Double.class)
public abstract class VertexDoubleConsumerMixin implements IAcceleratedVertexConsumer, VertexConsumer {

    @Shadow @Final private VertexConsumer first;
    @Shadow @Final private VertexConsumer second;

    @Unique private Map<RenderType, VertexConsumer> vertexConsumers;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void getRenderTypes(
            VertexConsumer pFirst,
            VertexConsumer pSecond,
            CallbackInfo ci
    ) {
        this.vertexConsumers = new Object2ObjectLinkedOpenHashMap<>();

        ((IAcceleratedVertexConsumer) pFirst).mapRenderTypes(this.vertexConsumers);
        ((IAcceleratedVertexConsumer) pSecond).mapRenderTypes(this.vertexConsumers);
    }

    @Unique
    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        ((IAcceleratedVertexConsumer) first).beginTransform(transformMatrix, normalMatrix);
        ((IAcceleratedVertexConsumer) second).beginTransform(transformMatrix, normalMatrix);
    }

    @Unique
    @Override
    public void endTransform() {
        ((IAcceleratedVertexConsumer) first).endTransform();
        ((IAcceleratedVertexConsumer) second).endTransform();
    }

    @Unique
    @Override
    public boolean isAccelerated() {
        return ((IAcceleratedVertexConsumer) first).isAccelerated()
                && ((IAcceleratedVertexConsumer) second).isAccelerated();
    }

    @Unique
    @Override
    public void mapRenderTypes(Map<RenderType, VertexConsumer> map) {
        map.putAll(this.vertexConsumers);
    }

    @Unique
    @Override
    public Set<RenderType> getRenderTypes() {
        return vertexConsumers.keySet();
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
        ((IAcceleratedVertexConsumer) vertexConsumers.getOrDefault(renderType, ThrowingVertexConsumer.INSTANCE)).addClientMesh(
                renderType,
                meshBuffer,
                size,
                color,
                light,
                overlay
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
        ((IAcceleratedVertexConsumer) vertexConsumers.getOrDefault(renderType, ThrowingVertexConsumer.INSTANCE)).addServerMesh(
                renderType,
                offset,
                size,
                color,
                light,
                overlay
        );
    }

    @Override
    public VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ
    ) {
        first.addVertex(
                pPose,
                pX,
                pY,
                pZ
        );
        second.addVertex(
                pPose,
                pX,
                pY,
                pZ
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
        first.setNormal(
                pPose,
                pNormalX,
                pNormalY,
                pNormalZ
        );
        second.setNormal(
                pPose,
                pNormalX,
                pNormalY,
                pNormalZ
        );

        return this;
    }
}

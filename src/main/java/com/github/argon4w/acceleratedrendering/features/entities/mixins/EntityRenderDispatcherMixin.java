package com.github.argon4w.acceleratedrendering.features.entities.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "shadowVertex", at = @At("HEAD"), cancellable = true)
    private static void fastShadowVertex(
            PoseStack.Pose pPose,
            VertexConsumer pConsumer,
            int pColor,
            float pOffsetX,
            float pOffsetY,
            float pOffsetZ,
            float pU,
            float pV,
            CallbackInfo ci
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) pConsumer;

        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        if (!AcceleratedEntityRenderingFeature.shouldUseAcceleratedPipeline()) {
            return;
        }

        if (!extension.isAccelerated()) {
            return;
        }

        ci.cancel();

        extension.beginTransform(pPose.pose(), pPose.normal());
        pConsumer.addVertex(
                pOffsetX,
                pOffsetY,
                pOffsetZ,
                pColor,
                pU,
                pV,
                OverlayTexture.NO_OVERLAY,
                LightTexture.FULL_BRIGHT,
                0.0F,
                1.0F,
                0.0F
        );
    }

    @Inject(method = "fireVertex", at = @At("HEAD"), cancellable = true)
    private static void fastFlameVertex(
            PoseStack.Pose pMatrixEntry,
            VertexConsumer pBuffer,
            float pX,
            float pY,
            float pZ,
            float pTexU,
            float pTexV,
            CallbackInfo ci
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) pBuffer;

        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        if (!AcceleratedEntityRenderingFeature.shouldUseAcceleratedPipeline()) {
            return;
        }

        if (!extension.isAccelerated()) {
            return;
        }

        ci.cancel();

        extension.beginTransform(pMatrixEntry.pose(), pMatrixEntry.normal());
        pBuffer.addVertex(
                pX,
                pY,
                pZ,
                -1,
                pTexU,
                pTexV,
                OverlayTexture.NO_OVERLAY,
                LightTexture.FULL_BLOCK,
                0.0F,
                1.0F,
                0.0F
        );
    }
}

package com.github.argon4w.acceleratedrendering.features.entities.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.AcceleratedOutlineBufferSource;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow public abstract boolean shouldShowEntityOutlines();

    @Shadow @Final private Minecraft minecraft;

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
    public void wrapRenderEntity(LevelRenderer instance, Entity pEntity, double pCamX, double pCamY, double pCamZ, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, Operation<Void> original, @Local(name = "flag2") LocalBooleanRef flag2) {
        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            original.call(instance, pEntity, pCamX, pCamY, pCamZ, pPartialTick, pPoseStack, pBufferSource);
            return;
        }

        MultiBufferSource bufferSource = AcceleratedBufferSource.CORE;

        if (shouldShowEntityOutlines()) {
            if (minecraft.shouldEntityAppearGlowing(pEntity)) {
                bufferSource = AcceleratedOutlineBufferSource.OUTLINE.setTeamColor(pEntity.getTeamColor());
                flag2.set(true);
            } else if (pEntity.hasCustomOutlineRendering(minecraft.player)) {
                flag2.set(true);
            }
        }

        original.call(instance, pEntity, pCamX, pCamY, pCamZ, pPartialTick, pPoseStack, bufferSource);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V", ordinal = 0))
    public void endAllEntityBatches(DeltaTracker pDeltaTracker, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pFrustumMatrix, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        AcceleratedBufferSource.CORE.drawBuffers();
        AcceleratedBufferSource.CORE.clearBuffers();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V"))
    public void endOutlineBatches(DeltaTracker pDeltaTracker, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pFrustumMatrix, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        AcceleratedOutlineBufferSource.OUTLINE.drawBuffers();
        AcceleratedOutlineBufferSource.OUTLINE.clearBuffers();
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    public void checkControllerStack(DeltaTracker pDeltaTracker, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pFrustumMatrix, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        AcceleratedEntityRenderingFeature.checkControllerState();
    }
}

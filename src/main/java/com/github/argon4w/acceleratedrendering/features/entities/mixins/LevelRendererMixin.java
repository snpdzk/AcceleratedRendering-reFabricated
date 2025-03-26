package com.github.argon4w.acceleratedrendering.features.entities.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract boolean shouldShowEntityOutlines();

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
    public void wrapRenderEntity(
            LevelRenderer instance,
            Entity pEntity,
            double pCamX,
            double pCamY,
            double pCamZ,
            float pPartialTick,
            PoseStack pPoseStack,
            MultiBufferSource pBufferSource,
            Operation<Void> original,
            @Local(index = 23) LocalBooleanRef flag2
    ) {
        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            original.call(
                    instance,
                    pEntity,
                    pCamX,
                    pCamY,
                    pCamZ,
                    pPartialTick,
                    pPoseStack,
                    pBufferSource
            );
            return;
        }

        if (!shouldShowEntityOutlines()) {
            original.call(
                    instance,
                    pEntity,
                    pCamX,
                    pCamY,
                    pCamZ,
                    pPartialTick,
                    pPoseStack,
                    CoreBuffers.CORE
            );
            return;
        }

        if (minecraft.shouldEntityAppearGlowing(pEntity)) {
            CoreBuffers.CORE_OUTLINE.setColor(pEntity.getTeamColor());

            original.call(
                    instance,
                    pEntity,
                    pCamX,
                    pCamY,
                    pCamZ,
                    pPartialTick,
                    pPoseStack,
                    CoreBuffers.CORE_OUTLINE
            );
            flag2.set(true);
            return;
        }

//        if (pEntity.hasCustomOutlineRendering(minecraft.player)) {
//            flag2.set(true);
//        }

        original.call(
                instance,
                pEntity,
                pCamX,
                pCamY,
                pCamZ,
                pPartialTick,
                pPoseStack,
                CoreBuffers.CORE
        );
    }
}

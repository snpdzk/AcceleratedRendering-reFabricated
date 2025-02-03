package com.github.argon4w.acceleratedrendering.features.entities.mixins;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(ShadowRenderer.class)
public class ShadowRendererMixin {

    @Inject(method = "renderShadows", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"))
    public void endAllBatches(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci) {
        if (!IrisCompatFeature.isIrisCompatEntitiesEnabled()) {
            return;
        }

        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        IrisCompatFeature.ENTITY_SHADOW.drawBuffers();
        IrisCompatFeature.SHADOW_BATCHING.drawBuffers();
        IrisCompatFeature.ENTITY_SHADOW.clearBuffers();
        IrisCompatFeature.SHADOW_BATCHING.clearBuffers();
        IrisCompatFeature.SHADOW_VANILLA_RENDER_BUFFERS.bufferSource().endBatch();
    }

    @ModifyArg(method = "renderShadows", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/shadows/ShadowRenderer;renderEntities(Lnet/irisshaders/iris/mixin/LevelRendererAccessor;Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;FLnet/minecraft/client/renderer/culling/Frustum;DDD)I"), index = 2)
    public MultiBufferSource.BufferSource useAcceleratedBufferSource(MultiBufferSource.BufferSource bufferSource) {
        if (!IrisCompatFeature.isIrisCompatEntitiesEnabled()) {
            return bufferSource;
        }

        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return bufferSource;
        }

        return IrisCompatFeature.SHADOW;
    }
}

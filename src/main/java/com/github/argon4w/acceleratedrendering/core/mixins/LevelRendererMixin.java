package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V"))
    public void endOutlineBatches(
            DeltaTracker pDeltaTracker,
            boolean pRenderBlockOutline,
            Camera pCamera,
            GameRenderer pGameRenderer,
            LightTexture pLightTexture,
            Matrix4f pFrustumMatrix,
            Matrix4f pProjectionMatrix,
            CallbackInfo ci
    ) {
        CoreBuffers.POS_TEX_COLOR.drawBuffers();
        CoreBuffers.POS_TEX_COLOR.clearBuffers();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V", ordinal = 0))
    public void endAllEntityBatches(
            DeltaTracker pDeltaTracker,
            boolean pRenderBlockOutline,
            Camera pCamera,
            GameRenderer pGameRenderer,
            LightTexture pLightTexture,
            Matrix4f pFrustumMatrix,
            Matrix4f pProjectionMatrix,
            CallbackInfo ci
    ) {
        CoreBuffers.ENTITY.drawBuffers();
        CoreBuffers.POS_TEX.drawBuffers();
        CoreBuffers.ENTITY.clearBuffers();
        CoreBuffers.POS_TEX.clearBuffers();
    }
}

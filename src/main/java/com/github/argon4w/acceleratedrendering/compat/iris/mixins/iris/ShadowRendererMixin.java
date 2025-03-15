package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris;

import com.github.argon4w.acceleratedrendering.compat.iris.IShadowBufferSourceGetter;
import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatBuffers;
import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingBufferSource;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ShadowRenderer.class)
public class ShadowRendererMixin implements IShadowBufferSourceGetter {

    @Shadow @Final private RenderBuffers buffers;

    @Unique
    private final Supplier<MultiBufferSource.BufferSource> SHADOW = Suppliers.memoize(() -> RedirectingBufferSource.builder()
            .fallback(buffers.bufferSource())
            .bufferSource(IrisCompatBuffers.ENTITY_SHADOW)
            .bufferSource(IrisCompatBuffers.GLYPH_SHADOW)
            .bufferSource(IrisCompatBuffers.POS_TEX_SHADOW)
            .mode(VertexFormat.Mode.QUADS)
            .mode(VertexFormat.Mode.TRIANGLES)
            .fallbackName("breeze_wind")
            .fallbackName("energy_swirl")
            .build());

    @Inject(method = "renderShadows", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"))
    public void endAllBatches(
            LevelRendererAccessor levelRenderer,
            Camera playerCamera,
            CallbackInfo ci
    ) {
        IrisCompatBuffers.ENTITY_SHADOW.drawBuffers();
        IrisCompatBuffers.GLYPH_SHADOW.drawBuffers();
        IrisCompatBuffers.POS_TEX_SHADOW.drawBuffers();
        IrisCompatBuffers.ENTITY_SHADOW.clearBuffers();
        IrisCompatBuffers.POS_TEX_SHADOW.clearBuffers();
        IrisCompatBuffers.GLYPH_SHADOW.clearBuffers();
    }

    @Inject(method = "renderShadows", at = @At("TAIL"))
    public void checkControllerState(
            LevelRendererAccessor levelRenderer,
            Camera playerCamera,
            CallbackInfo ci
    ) {
        IrisCompatFeature.checkControllerState();
    }

    @Unique
    @Override
    public MultiBufferSource.BufferSource getShadowBufferSource() {
        return SHADOW.get();
    }
}

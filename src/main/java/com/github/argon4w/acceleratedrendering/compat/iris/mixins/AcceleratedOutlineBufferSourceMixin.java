package com.github.argon4w.acceleratedrendering.compat.iris.mixins;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisRenderType;
import com.github.argon4w.acceleratedrendering.core.buffers.AcceleratedOutlineBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.IAcceleratedBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AcceleratedOutlineBufferSource.class)
public abstract class AcceleratedOutlineBufferSourceMixin extends AcceleratedBufferSourceMixin {

    @Shadow @Final private IAcceleratedBufferSource bufferSource;

    @ModifyArg(method = "getBuffer", at = @At(value = "INVOKE", target = "Lcom/github/argon4w/acceleratedrendering/core/buffers/builders/AcceleratedOutlineGenerator;<init>(Lcom/github/argon4w/acceleratedrendering/core/buffers/environments/IBufferEnvironment;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/RenderType;I)V"), index = 2)
    public RenderType unwrapRenderType(RenderType renderType) {
        return new IrisRenderType(
                renderType,
                getBufferEnvironment().getVertexFormat(renderType)
        );
    }

    @ModifyArg(method = "getBuffer", at = @At(value = "INVOKE", target = "Lcom/github/argon4w/acceleratedrendering/core/buffers/builders/AcceleratedDoubleVertexConsumer;<init>(Lcom/github/argon4w/acceleratedrendering/core/buffers/environments/IBufferEnvironment;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"), index = 1)
    public RenderType wrapCorrectFormat1(RenderType renderType) {
        return new IrisRenderType(
                renderType,
                bufferSource.getBufferEnvironment().getVertexFormat(renderType)
        );
    }

    @ModifyArg(method = "getBuffer", at = @At(value = "INVOKE", target = "Lcom/github/argon4w/acceleratedrendering/core/buffers/builders/AcceleratedDoubleVertexConsumer;<init>(Lcom/github/argon4w/acceleratedrendering/core/buffers/environments/IBufferEnvironment;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"), index = 3)
    public RenderType wrapCorrectFormat2(RenderType renderType) {

        return new IrisRenderType(
                renderType,
                getBufferEnvironment().getVertexFormat(renderType)
        );
    }
}

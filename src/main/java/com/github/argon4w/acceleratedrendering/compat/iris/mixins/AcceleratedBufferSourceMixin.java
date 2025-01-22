package com.github.argon4w.acceleratedrendering.compat.iris.mixins;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisRenderType;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AcceleratedBufferSource.class)
public abstract class AcceleratedBufferSourceMixin {

    @Shadow @Final private IBufferEnvironment bufferEnvironment;

    @ModifyArg(method = "getBuffer", at = @At(value = "INVOKE", target = "Lcom/github/argon4w/acceleratedrendering/core/buffers/builders/AcceleratedBufferBuilder;create(Lcom/github/argon4w/acceleratedrendering/core/buffers/accelerated/ElementBuffer;Lcom/github/argon4w/acceleratedrendering/core/buffers/environments/IBufferEnvironment;Lcom/github/argon4w/acceleratedrendering/core/buffers/accelerated/AcceleratedBufferSetPool$BufferSet;Lnet/minecraft/client/renderer/RenderType;)Lcom/github/argon4w/acceleratedrendering/core/buffers/builders/AcceleratedBufferBuilder;"), index = 3)
    public RenderType unwrapIrisRenderType(RenderType renderType) {
        return new IrisRenderType(
                renderType,
                bufferEnvironment.getVertexFormat(renderType)
        );
    }
}

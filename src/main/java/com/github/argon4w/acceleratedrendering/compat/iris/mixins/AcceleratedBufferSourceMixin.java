package com.github.argon4w.acceleratedrendering.compat.iris.mixins;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisBufferEnvironment;
import com.github.argon4w.acceleratedrendering.compat.iris.IrisRenderType;
import com.github.argon4w.acceleratedrendering.core.buffers.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AcceleratedBufferSource.class)
public abstract class AcceleratedBufferSourceMixin {

    @Shadow public abstract IBufferEnvironment getBufferEnvironment();

    @ModifyArg(method = "initCoreBufferSource", at = @At(value = "INVOKE", target = "Lcom/github/argon4w/acceleratedrendering/core/buffers/AcceleratedBufferSource;<init>(Lcom/github/argon4w/acceleratedrendering/core/buffers/environments/IBufferEnvironment;)V"), index = 0)
    private static IBufferEnvironment useIrisBufferEnvironment(IBufferEnvironment bufferEnvironment) {
        return IrisBufferEnvironment.CORE;
    }

    @ModifyArg(method = "getAcceleratedBuffer", at = @At(value = "INVOKE", target = "Lcom/github/argon4w/acceleratedrendering/core/buffers/builders/AcceleratedBufferBuilder;create(Lcom/github/argon4w/acceleratedrendering/core/buffers/ElementBuffer;Lcom/github/argon4w/acceleratedrendering/core/buffers/environments/IBufferEnvironment;Lcom/github/argon4w/acceleratedrendering/core/buffers/AcceleratedBufferSetPool$BufferSet;Lnet/minecraft/client/renderer/RenderType;)Lcom/github/argon4w/acceleratedrendering/core/buffers/builders/AcceleratedBufferBuilder;"), index = 3)
    public RenderType unwrapIrisRenderType(RenderType renderType) {
        return new IrisRenderType(
                renderType,
                getBufferEnvironment().getVertexFormat(renderType)
        );
    }
}

package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingBufferSource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedirectingBufferSource.class)
public class RedirectingBufferSourceMixin {

    @WrapOperation(method = "getBuffer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderType;name:Ljava/lang/String;"))
    public String unwrapIrisRenderType(RenderType instance, Operation<String> original) {
        return original.call(instance instanceof WrappableRenderType wrappable ? wrappable.unwrap() : instance);
    }
}

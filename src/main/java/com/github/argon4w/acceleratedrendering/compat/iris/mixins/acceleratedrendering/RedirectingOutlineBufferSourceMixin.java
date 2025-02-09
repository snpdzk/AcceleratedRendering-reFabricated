package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.buffers.IRenderTypeExtension;
import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.redirecting.RedirectingOutlineBufferSource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedirectingOutlineBufferSource.class)
public class RedirectingOutlineBufferSourceMixin {

    @WrapOperation(method = "getBuffer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderType;name:Ljava/lang/String;"))
    public String unwrapIrisRenderType(RenderType instance, Operation<String> original) {
        IRenderTypeExtension extension = (IRenderTypeExtension) instance;

        if (!IrisCompatFeature.isEnabled()) {
            return original.call(instance);
        }

        if (extension.isFastUnwrapSupported()) {
            return original.call(extension.getOrUnwrap());
        }

        if (IrisCompatFeature.isFastIrisRenderTypeCheckEnabled()) {
            return original.call(extension.getOrUnwrap());
        }

        if (!(instance instanceof WrappableRenderType wrappable)) {
            return original.call(instance);
        }

        return original.call(wrappable.unwrap());
    }
}

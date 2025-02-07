package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.IRenderTypeExtension;
import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderTypeUtils.class)
public class RenderTypeUtilsMixin {

    @ModifyVariable(method = "getTextureLocation", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static RenderType unwrapIrisRenderType1(RenderType renderType) {
        if (!IrisCompatFeature.isEnabled()) {
            return renderType;
        }

        if (IrisCompatFeature.isFastIrisRenderTypeCheckEnabled()) {
            return ((IRenderTypeExtension) renderType).getOrUnwrap();
        }

        if (!(renderType instanceof WrappableRenderType wrappable)) {
            return renderType;
        }

        return wrappable.unwrap();
    }

    @ModifyVariable(method = "isCulled", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static RenderType unwrapIrisRenderType2(RenderType renderType) {
        if (!IrisCompatFeature.isEnabled()) {
            return renderType;
        }

        if (IrisCompatFeature.isFastIrisRenderTypeCheckEnabled()) {
            return ((IRenderTypeExtension) renderType).getOrUnwrap();
        }

        if (!(renderType instanceof WrappableRenderType wrappable)) {
            return renderType;
        }

        return wrappable.unwrap();
    }
}

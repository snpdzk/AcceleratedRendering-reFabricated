package com.github.argon4w.acceleratedrendering.compat.iris.mixins;

import com.github.argon4w.acceleratedrendering.core.utils.TextureUtils;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextureUtils.class)
public class TextureUtilsMixin {

    @ModifyVariable(method = "downloadTexture", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static RenderType unwrapIrisRenderType(RenderType renderType) {
        return renderType instanceof WrappableRenderType wrappable ? wrappable.unwrap() : renderType;
    }
}

package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShadowRenderer.class)
public class ShadowRendererMixin {

    @Inject(method = "renderShadows", at = @At("TAIL"))
    public void checkControllerState(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci) {
        IrisCompatFeature.checkControllerState();
    }
}

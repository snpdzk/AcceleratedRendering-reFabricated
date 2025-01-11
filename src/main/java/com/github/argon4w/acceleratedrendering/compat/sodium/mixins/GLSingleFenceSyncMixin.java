package com.github.argon4w.acceleratedrendering.compat.sodium.mixins;

import com.github.argon4w.acceleratedrendering.core.gl.FenceSync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FenceSync.class)
public class GLSingleFenceSyncMixin {

    @Inject(method = "fenceSync", at = @At("HEAD"), cancellable = true)
    public void useSodiumFenceSync(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "clientWaitSync", at = @At("HEAD"), cancellable = true)
    public void useSodiumWaitSync(CallbackInfo ci) {
        ci.cancel();
    }
}

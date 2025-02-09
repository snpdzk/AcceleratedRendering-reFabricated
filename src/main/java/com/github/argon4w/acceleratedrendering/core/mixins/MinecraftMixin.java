package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "runTick", at = @At("TAIL"))
    public void checkControllerState(boolean pRenderLevel, CallbackInfo ci) {
        CoreFeature.checkControllerState();
    }
}

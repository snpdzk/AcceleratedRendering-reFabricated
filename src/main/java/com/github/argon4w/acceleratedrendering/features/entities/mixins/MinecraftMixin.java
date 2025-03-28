package com.github.argon4w.acceleratedrendering.features.entities.mixins;

import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "runTick", at = @At("TAIL"))
    public void checkControllerState(boolean pRenderLevel, CallbackInfo ci) {
        AcceleratedEntityRenderingFeature.checkControllerState();
    }
}

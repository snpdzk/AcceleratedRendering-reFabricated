package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Final private ReloadableResourceManager resourceManager;

    @Inject(method = "runTick", at = @At("TAIL"))
    public void checkControllerState(boolean pRenderLevel, CallbackInfo ci) {
        CoreFeature.checkControllerState();
    }


    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;updateVsync(Z)V"))
    void onRegisterClientReloadListeners(GameConfig gameConfig, CallbackInfo ci) {
        this.resourceManager.registerReloadListener(ComputeShaderProgramLoader.INSTANCE);
    }
}

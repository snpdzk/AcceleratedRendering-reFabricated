package com.example.examplemod.mixins;

import com.example.examplemod.IMeshDataExtension;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class)
public class RenderTypeMixin extends RenderStateShardMixin {
    @Inject(method = "draw", at = @At("HEAD"))
    public void draw(MeshData pMeshData, CallbackInfo ci) {
        ((IMeshDataExtension) pMeshData).sme$SetRenderType((RenderType) (Object) this);
    }
}

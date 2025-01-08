package com.github.argon4w.acceleratedrendering.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

@Mixin(MeshData.class)
public class MeshDataMixin {
    @Shadow @Final private ByteBufferBuilder.Result vertexBuffer;

    @Inject(method = "vertexBuffer", at = @At("HEAD"), cancellable = true)
    public void allowNullVertexBuffer1(CallbackInfoReturnable<ByteBuffer> cir) {
        if (vertexBuffer == null) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }

    @WrapOperation(method = "close", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/ByteBufferBuilder$Result;close()V", ordinal = 0))
    public void allowNullVertexBuffer2(ByteBufferBuilder.Result instance, Operation<Void> original) {
        if (instance != null) {
            original.call(instance);
        }
    }
}

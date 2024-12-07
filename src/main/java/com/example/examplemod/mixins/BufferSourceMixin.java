package com.example.examplemod.mixins;

import com.example.examplemod.IBufferBuilderExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(MultiBufferSource.BufferSource.class)
public class BufferSourceMixin {
    @WrapOperation(method = "getBuffer", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object getBuffer(Map<?, ?> instance, Object k, Object v, Operation<Object> original) {
        ((IBufferBuilderExtension) v).sme$setRenderType((RenderType) k);
        return original.call(instance, k, v);
    }
}

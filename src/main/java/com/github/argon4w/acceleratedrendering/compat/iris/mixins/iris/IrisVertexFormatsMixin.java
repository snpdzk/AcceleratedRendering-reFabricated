package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = IrisVertexFormats.class, priority = Integer.MAX_VALUE)
public class IrisVertexFormatsMixin {

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexFormat$Builder;build()Lcom/mojang/blaze3d/vertex/VertexFormat;"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/irisshaders/iris/vertices/IrisVertexFormats;TERRAIN:Lcom/mojang/blaze3d/vertex/VertexFormat;", opcode = Opcodes.PUTSTATIC),
                    to = @At(value = "FIELD", target = "Lnet/irisshaders/iris/vertices/IrisVertexFormats;ENTITY:Lcom/mojang/blaze3d/vertex/VertexFormat;", opcode = Opcodes.PUTSTATIC))
    )
    private static VertexFormat addPaddingForEntityFormat(VertexFormat.Builder instance, Operation<VertexFormat> original) {
        return original.call(instance.padding(2));
    }
}

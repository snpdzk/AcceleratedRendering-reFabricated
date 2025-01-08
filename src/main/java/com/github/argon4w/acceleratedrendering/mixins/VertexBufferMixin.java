package com.github.argon4w.acceleratedrendering.mixins;

import com.github.argon4w.acceleratedrendering.buffers.BatchedEntityBufferSource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {

    @WrapOperation(method = "upload", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;uploadVertexBuffer(Lcom/mojang/blaze3d/vertex/MeshData$DrawState;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/vertex/VertexFormat;"))
    public VertexFormat uploadAcceleratedVertexBuffer(VertexBuffer instance, MeshData.DrawState pDrawState, ByteBuffer pBuffer, Operation<VertexFormat> original) {
        if (!pDrawState.format().equals(DefaultVertexFormat.NEW_ENTITY)) {
            return original.call(instance, pDrawState, pBuffer);
        }

        if (!BatchedEntityBufferSource.isAcceleratedRendering()) {
            return original.call(instance, pDrawState, pBuffer);
        }

        glBindBuffer(GL_ARRAY_BUFFER, BatchedEntityBufferSource.INSTANCE.getVertexBuffer().getBufferHandle());
        pDrawState.format().setupBufferState();

        return null;
    }
}

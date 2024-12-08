package com.example.examplemod.mixins;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.IMeshDataExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {
    @Shadow private int vertexBufferId;

    @Shadow @Final private VertexBuffer.Usage usage;
    @Shadow @Nullable private VertexFormat format;
    @Unique private static final int sme$TRANSFORM_BUFFER_GL_POINTER;
    @Unique private static final int sme$NORMAL_BUFFER_GL_POINTER;
    @Unique private static final int sme$TRANSFORM_INDEX_BUFFER_GL_POINTER;
    @Unique private int sme$vertexBufferSize = 0;

    static {
        sme$TRANSFORM_BUFFER_GL_POINTER = glCreateBuffers();
        sme$NORMAL_BUFFER_GL_POINTER = glCreateBuffers();
        sme$TRANSFORM_INDEX_BUFFER_GL_POINTER = glCreateBuffers();

        glNamedBufferStorage(sme$TRANSFORM_BUFFER_GL_POINTER, 4 * 4 * 4 * 32768 * 32, GL_DYNAMIC_STORAGE_BIT);
        glNamedBufferStorage(sme$NORMAL_BUFFER_GL_POINTER, 4 * 4 * 3 * 32768 * 32, GL_DYNAMIC_STORAGE_BIT);
        glNamedBufferStorage(sme$TRANSFORM_INDEX_BUFFER_GL_POINTER, 4 * 32768 * 32, GL_DYNAMIC_STORAGE_BIT);
    }

    /*@WrapOperation(method = "uploadVertexBuffer", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;glBufferData(ILjava/nio/ByteBuffer;I)V"))
    public void uploadVertexBuffer(int pTarget, ByteBuffer pData, int pUsage, Operation<Void> original) {
        if (format != DefaultVertexFormat.NEW_ENTITY) {
            original.call(pTarget, pData, pUsage);
            return;
        }

        if (sme$vertexBufferSize < pData.capacity()) {
            glBufferData(34962, pData.capacity(), this.usage.id);
            sme$vertexBufferSize = pData.capacity();
        }

        MemoryUtil.memCopy(pData, glMapBuffer(34962, GL_WRITE_ONLY));
        glUnmapBuffer(34962);
    }*/

    @Inject(method = "upload", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;uploadVertexBuffer(Lcom/mojang/blaze3d/vertex/MeshData$DrawState;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/vertex/VertexFormat;", shift = At.Shift.AFTER))
    public void upload(MeshData pMeshData, CallbackInfo ci) {
        IMeshDataExtension extension = (IMeshDataExtension) pMeshData;

        if (extension.sme$getRenderType() == null) {
            return;
        }

        if (extension.sme$getRenderType().format != DefaultVertexFormat.NEW_ENTITY) {
            return;
        }

        try (
                var transformIndexBuffer = extension.sme$getTransformIndexBuffer();
                var transformBuffer = extension.sme$getTransformBuffer();
                var normalBuffer = extension.sme$getNormalBuffer()
        ) {
            if (transformIndexBuffer == null) {
                return;
            }

            if (transformBuffer == null) {
                return;
            }

            if (normalBuffer == null) {
                return;
            }

            glNamedBufferSubData(sme$TRANSFORM_INDEX_BUFFER_GL_POINTER, 0, transformIndexBuffer.byteBuffer());
            glNamedBufferSubData(sme$TRANSFORM_BUFFER_GL_POINTER, 0, transformBuffer.byteBuffer());
            glNamedBufferSubData(sme$NORMAL_BUFFER_GL_POINTER, 0, normalBuffer.byteBuffer());

            glUseProgram(ExampleMod.program);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, vertexBufferId);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, sme$TRANSFORM_BUFFER_GL_POINTER);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, sme$NORMAL_BUFFER_GL_POINTER);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, sme$TRANSFORM_INDEX_BUFFER_GL_POINTER);

            glDispatchCompute(pMeshData.vertexBuffer().capacity() / pMeshData.drawState().format().getVertexSize(), 1, 1);
            glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        }
    }
}

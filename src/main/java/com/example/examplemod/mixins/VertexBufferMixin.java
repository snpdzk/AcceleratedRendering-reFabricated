package com.example.examplemod.mixins;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.IMeshDataExtension;
import com.example.examplemod.SMEBuffers;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import static org.lwjgl.opengl.GL44.GL_DYNAMIC_STORAGE_BIT;
import static org.lwjgl.opengl.GL45.*;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {
    @Shadow private int vertexBufferId;

    @Unique private static final int sme$TRANSFORM_BUFFER_GL_POINTER;
    @Unique private static final int sme$TRANSFORM_INDEX_BUFFER_GL_POINTER;

    static {
        sme$TRANSFORM_BUFFER_GL_POINTER = glCreateBuffers();
        sme$TRANSFORM_INDEX_BUFFER_GL_POINTER = glCreateBuffers();

        glNamedBufferStorage(sme$TRANSFORM_BUFFER_GL_POINTER, 4 * 4 * 4 * 32768 * 32, GL_DYNAMIC_STORAGE_BIT);
        glNamedBufferStorage(sme$TRANSFORM_INDEX_BUFFER_GL_POINTER, 4 * 32768 * 32, GL_DYNAMIC_STORAGE_BIT);
    }

    @Inject(method = "upload", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;uploadVertexBuffer(Lcom/mojang/blaze3d/vertex/MeshData$DrawState;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/vertex/VertexFormat;", shift = At.Shift.AFTER))
    public void upload(MeshData pMeshData, CallbackInfo ci) {
        IMeshDataExtension extension = (IMeshDataExtension) pMeshData;

        if (extension.sme$getRenderType() == null) {
            return;
        }

        if (!extension.sme$getRenderType().name.contains("entity_cutout")) {
            return;
        }

        try (var transformIndexBuffer = extension.sme$getTransformIndexBuffer(); var transformBuffer = extension.sme$getTransformBuffer()) {
            if (transformIndexBuffer == null) {
                return;
            }

            if (transformBuffer == null) {
                return;
            }

            glNamedBufferSubData(sme$TRANSFORM_INDEX_BUFFER_GL_POINTER, 0, transformIndexBuffer.byteBuffer());
            glNamedBufferSubData(sme$TRANSFORM_BUFFER_GL_POINTER, 0, transformBuffer.byteBuffer());

            glUseProgram(ExampleMod.program);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, vertexBufferId);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, sme$TRANSFORM_BUFFER_GL_POINTER);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, sme$TRANSFORM_INDEX_BUFFER_GL_POINTER);

            glDispatchCompute(pMeshData.vertexBuffer().capacity() / pMeshData.drawState().format().getVertexSize() / 4, 1, 1);
            glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        }

        SMEBuffers.TRANSFORM_INDEX_BUFFER.clear();
        SMEBuffers.TRANSFORM_BUFFER.clear();
    }
}

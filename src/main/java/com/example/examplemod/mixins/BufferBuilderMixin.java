package com.example.examplemod.mixins;

import com.example.examplemod.IBufferBuilderExtension;
import com.example.examplemod.IEntityBufferSet;
import com.example.examplemod.IMeshDataExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements IBufferBuilderExtension {
    @Shadow @Final private int vertexSize;
    @Shadow @Final private ByteBufferBuilder buffer;
    @Shadow @Final private boolean fastFormat;

    @Shadow protected abstract void ensureBuilding();
    @Shadow protected abstract void endLastVertex();
    @Shadow private int vertices;

    @Unique private int sme$transformIndex = -1;
    @Unique private RenderType sme$renderType;

    @Unique private ByteBufferBuilder sme$transformIndexBuffer;
    @Unique private ByteBufferBuilder sme$transformBuffer;
    @Unique private ByteBufferBuilder sme$normalBuffer;
    @Unique private boolean sme$supplied = false;

    @ModifyReturnValue(method = "storeMesh", at = @At("RETURN"))
    public MeshData modifyMeshData(MeshData original) {
        if (original == null) {
            return null;
        }

        if (!sme$supplied) {
            return original;
        }

        IMeshDataExtension extension = (IMeshDataExtension) original;
        extension.sme$setTransformBuffer(sme$transformBuffer.build());
        extension.sme$setNormalBuffer(sme$normalBuffer.build());
        extension.sme$setTransformIndexBuffer(sme$transformIndexBuffer.build());

        return (MeshData) extension;
    }

    @Override
    public void sme$supply(IEntityBufferSet bufferSet, RenderType renderType) {
        sme$transformBuffer = bufferSet.transformBuffer();
        sme$normalBuffer = bufferSet.normalBuffer();
        sme$transformIndexBuffer = bufferSet.transformIndexBuffer();
        sme$renderType = renderType;
        sme$supplied = true;
    }

    @Unique
    @Override
    public void sme$beginTransform(PoseStack.Pose pose) {
        if (!sme$supplied) {
            return;
        }

        if (sme$renderType.format != DefaultVertexFormat.NEW_ENTITY) {
            return;
        }

        sme$transformIndex ++;

        long transformPointer = sme$transformBuffer.reserve(4 * 4 * 4);
        long normalPointer = sme$normalBuffer.reserve(4 * 4 * 3);

        pose.pose().get(MemoryUtil.memByteBuffer(transformPointer, 4 * 4 * 4));
        pose.normal().get3x4(MemoryUtil.memByteBuffer(normalPointer, 4 * 4 * 3));
    }

    @Override
    public void sme$addMesh(ByteBuffer vertexBuffer, int count) {
        if (!sme$supplied) {
            return;
        }

        if (vertexBuffer == null) {
            return;
        }

        if (sme$renderType.format != DefaultVertexFormat.NEW_ENTITY) {
            return;
        }

        if (!fastFormat) {
            return;
        }

        ensureBuilding();
        endLastVertex();

        vertices += count;

        long transformPointer = buffer.reserve(count * vertexSize);
        long indexPointer = sme$transformIndexBuffer.reserve(4 * count);

        MemoryUtil.memCopy(MemoryUtil.memAddress0(vertexBuffer), transformPointer, (long) count * vertexSize);

        for (int i = 0; i < count; i++) {
            MemoryUtil.memPutInt(indexPointer + i * 4L, sme$transformIndex);
        }
    }

    @Override
    public int sme$getVertices() {
        return vertices;
    }

    @Override
    public boolean sme$supportAcceleratedRendering() {
        return sme$supplied && sme$renderType.format == DefaultVertexFormat.NEW_ENTITY;
    }

    @Override
    public RenderType sme$getRenderType() {
        return sme$renderType;
    }
}

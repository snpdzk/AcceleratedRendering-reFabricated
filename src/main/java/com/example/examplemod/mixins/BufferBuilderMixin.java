package com.example.examplemod.mixins;

import com.example.examplemod.IBufferBuilderExtension;
import com.example.examplemod.IMeshDataExtension;
import com.example.examplemod.SMEBuffers;
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

    @ModifyReturnValue(method = "storeMesh", at = @At("RETURN"))
    public MeshData modifyMeshData(MeshData original) {
        if (original == null) {
            return null;
        }

        IMeshDataExtension extension = (IMeshDataExtension) original;
        extension.sme$setTransformBuffer(SMEBuffers.TRANSFORM_BUFFER.build());
        extension.sme$setTransformIndexBuffer(SMEBuffers.TRANSFORM_INDEX_BUFFER.build());

        return (MeshData) extension;
    }

    @Overwrite
    public static byte normalIntValue(float pValue) {
        return 0;
    }

    @Override
    public void sme$setRenderType(RenderType renderType) {
        sme$renderType = renderType;
    }

    @Override
    public void sme$setTransformIndex(int count) {
        if (sme$renderType == null) {
            return;
        }

        if (!sme$renderType.name.contains("entity_cutout")) {
            return;
        }

        long pointer = SMEBuffers.TRANSFORM_INDEX_BUFFER.reserve(4 * count);

        for (int i = 0; i < count; i++) {
            MemoryUtil.memPutInt(pointer + i * 4L, sme$transformIndex);
        }
    }

    @Unique
    @Override
    public void sme$beginTransform(PoseStack.Pose pose) {
        if (sme$renderType == null) {
            return;
        }

        if (!sme$renderType.name.contains("entity_cutout")) {
            return;
        }

        sme$transformIndex ++;
        long pointer = SMEBuffers.TRANSFORM_BUFFER.reserve(4 * 4 * 4);
        float[] values = new float[4 * 4];
        pose.pose().get(values);

        for (int i = 0; i < 16; i ++) {
            MemoryUtil.memPutFloat(pointer + i * 4, values[i]);
        }
    }

    @Override
    public void sme$addMesh(ByteBuffer byteBuffer, int count) {
        if (!sme$renderType.name.contains("entity_cutout")) {
            return;
        }

        if (!fastFormat) {
            return;
        }

        ensureBuilding();
        endLastVertex();

        long pointer = buffer.reserve(count * vertexSize);
        MemoryUtil.memCopy(MemoryUtil.memAddress0(byteBuffer), pointer, (long) count * vertexSize);
        vertices += count;
    }

    @Override
    public int sme$getVertices() {
        return vertices;
    }
}

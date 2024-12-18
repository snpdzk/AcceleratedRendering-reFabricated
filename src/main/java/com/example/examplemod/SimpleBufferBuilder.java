package com.example.examplemod;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SimpleBufferBuilder implements VertexConsumer, IBufferBuilderExtension {

    private static final boolean IS_LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    private static final Matrix4f NORMAL_MATRIX_4X4 = new Matrix4f();

    private final ByteBufferBuilder buffer;
    private int vertices;
    private final VertexFormat format;
    private final VertexFormat.Mode mode;
    private final int vertexSize;

    private int sme$transformIndex;
    private RenderType sme$renderType;

    private ByteBufferBuilder sme$transformIndexBuffer;
    private ByteBufferBuilder sme$transformBuffer;
    private ByteBufferBuilder sme$normalBuffer;
    private boolean sme$supplied;

    public SimpleBufferBuilder(ByteBufferBuilder pBuffer) {
        this.buffer = pBuffer;
        this.mode = VertexFormat.Mode.QUADS;
        this.format = DefaultVertexFormat.NEW_ENTITY;
        this.vertexSize = this.format.getVertexSize();

        sme$transformIndex = -1;
        sme$supplied = false;;
    }

    public MeshData build() {
        if (this.vertices == 0) {
            return null;
        }

        ByteBufferBuilder.Result bytebufferbuilder$result = this.buffer.build();

        if (bytebufferbuilder$result == null) {
            return null;
        }

        int i = this.mode.indexCount(this.vertices);
        VertexFormat.IndexType vertexformat$indextype = VertexFormat.IndexType.least(this.vertices);
        MeshData meshData = new MeshData(bytebufferbuilder$result, new MeshData.DrawState(this.format, this.vertices, i, this.mode, vertexformat$indextype));

        if (!sme$supplied) {
            return meshData;
        }

        IMeshDataExtension extension = (IMeshDataExtension) meshData;
        extension.sme$setTransformBuffer(sme$transformBuffer.build());
        extension.sme$setNormalBuffer(sme$normalBuffer.build());
        extension.sme$setTransformIndexBuffer(sme$transformIndexBuffer.build());

        return meshData;
    }

    @Override
    public VertexConsumer addVertex(float pX, float pY, float pZ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VertexConsumer setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VertexConsumer setNormal(float pNormalX, float pNormalY, float pNormalZ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addVertex(
            float pX,
            float pY,
            float pZ,
            int pColor,
            float pU,
            float pV,
            int pPackedOverlay,
            int pPackedLight,
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        this.vertices++;
        long i = this.buffer.reserve(this.vertexSize);

        MemoryUtil.memPutFloat(i + 0L, pX);
        MemoryUtil.memPutFloat(i + 4L, pY);
        MemoryUtil.memPutFloat(i + 8L, pZ);
        putRgba(i + 12L, pColor);
        MemoryUtil.memPutFloat(i + 16L, pU);
        MemoryUtil.memPutFloat(i + 20L, pV);
        putPackedUv(i + 24L, pPackedOverlay);
        putPackedUv(i + 28L, pPackedLight);
        MemoryUtil.memPutByte(i + 32L, normalIntValue(pNormalX));
        MemoryUtil.memPutByte(i + 33L, normalIntValue(pNormalY));
        MemoryUtil.memPutByte(i + 34L, normalIntValue(pNormalZ));
    }

    @Override
    public void sme$supply(IEntityBufferSet bufferSet, RenderType renderType) {
        sme$transformBuffer = bufferSet.transformBuffer();
        sme$normalBuffer = bufferSet.normalBuffer();
        sme$transformIndexBuffer = bufferSet.transformIndexBuffer();
        sme$renderType = renderType;
        sme$supplied = true;
    }

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
        NORMAL_MATRIX_4X4.set(pose.normal()).get3x4(MemoryUtil.memByteBuffer(normalPointer, 4 * 4 * 3)); //working
        //pose.normal().get3x4(MemoryUtil.memByteBuffer(normalPointer, 4 * 4 * 3)); //not working
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

    private static byte normalIntValue(float pValue) {
        return (byte)((int)(Mth.clamp(pValue, -1.0F, 1.0F) * 127.0F) & 0xFF);
    }

    private static void putRgba(long pPointer, int pColor) {
        int i = FastColor.ABGR32.fromArgb32(pColor);
        MemoryUtil.memPutInt(pPointer, IS_LITTLE_ENDIAN ? i : Integer.reverseBytes(i));
    }

    private static void putPackedUv(long pPointer, int pPackedUv) {
        if (IS_LITTLE_ENDIAN) {
            MemoryUtil.memPutInt(pPointer, pPackedUv);
        } else {
            MemoryUtil.memPutShort(pPointer, (short)(pPackedUv & 65535));
            MemoryUtil.memPutShort(pPointer + 2L, (short)(pPackedUv >> 16 & 65535));
        }
    }
}

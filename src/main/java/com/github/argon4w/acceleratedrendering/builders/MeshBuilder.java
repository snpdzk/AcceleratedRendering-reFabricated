package com.github.argon4w.acceleratedrendering.builders;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteOrder;

public abstract class MeshBuilder {

    private static final boolean LE = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

    private final ByteBufferBuilder buffer;
    private int vertices;

    private MeshBuilder(ByteBufferBuilder buffer) {
        this.buffer = buffer;
    }

    public abstract void putRgba(long pointer, int color);
    public abstract void putPackedUv(long pointer, int packedUv);

    public IMesh build() {
        if (this.vertices == 0) {
            return new EmptyMesh();
        }

        ByteBufferBuilder.Result result = this.buffer.build();

        if (result == null) {
            return new EmptyMesh();
        }

        return new OffHeapMesh(vertices, result.byteBuffer());
    }

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
        long i = this.buffer.reserve(DefaultVertexFormat.NEW_ENTITY.getVertexSize());

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

    public int getVertices() {
        return vertices;
    }

    private static byte normalIntValue(float pValue) {
        return (byte) ((int) (Mth.clamp(pValue, -1.0F, 1.0F) * 127.0F) & 0xFF);
    }

    public static MeshBuilder create(ByteBufferBuilder buffer) {
        return LE ? new LE(buffer) : new BE(buffer);
    }

    public static class BE extends MeshBuilder {

        private BE(ByteBufferBuilder buffer) {
            super(buffer);
        }

        @Override
        public void putRgba(long pointer, int color) {
            MemoryUtil.memPutInt(pointer, Integer.reverseBytes(FastColor.ABGR32.fromArgb32(color)));
        }

        @Override
        public void putPackedUv(long pointer, int packed) {
            MemoryUtil.memPutShort(pointer, (short) (packed & 65535));
            MemoryUtil.memPutShort(pointer + 2L, (short) (packed >> 16 & 65535));
        }
    }

    public static class LE extends MeshBuilder {

        private LE(ByteBufferBuilder buffer) {
            super(buffer);
        }

        @Override
        public void putRgba(long pointer, int color) {
            MemoryUtil.memPutInt(pointer, FastColor.ABGR32.fromArgb32(color));
        }

        @Override
        public void putPackedUv(long pointer, int packed) {
            MemoryUtil.memPutInt(pointer, packed);
        }
    }
}

package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IClientBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.ByteBufferUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteOrder;

public abstract class MeshCollector {

    private static final boolean LE = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

    private final RenderType key;
    private final VertexFormat vertexFormat;
    private final IClientBuffer buffer;
    private final int offset;
    private int vertexCount;

    private MeshCollector(
            RenderType key,
            IClientBuffer buffer,
            int offset
    ) {
        this.key = key;
        this.vertexFormat = key.format;
        this.buffer = buffer;
        this.vertexCount = 0;
        this.offset = offset;
    }

    public abstract void putRgba(long pointer, int color);
    public abstract void putPackedUv(long pointer, int packedUv);

    private int getSize() {
        return vertexFormat.getVertexSize();
    }

    private long getPosOffset() {
        return vertexFormat.getOffset(VertexFormatElement.POSITION);
    }

    private long getColorOffset() {
        return vertexFormat.getOffset(VertexFormatElement.COLOR);
    }

    private long getUvOffset() {
        return vertexFormat.getOffset(VertexFormatElement.UV);
    }

    private long getUv1Offset() {
        return vertexFormat.getOffset(VertexFormatElement.UV1);
    }

    public long getUv2Offset() {
        return vertexFormat.getOffset(VertexFormatElement.UV2);
    }

    public long getNormalOffset() {
        return vertexFormat.getOffset(VertexFormatElement.NORMAL);
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
        this.vertexCount++;

        long vertex = this.buffer.reserve(getSize());
        long posOffset = getPosOffset();
        long colorOffset = getColorOffset();
        long uv0Offset = getUvOffset();
        long uv1Offset = getUv1Offset();
        long uv2Offset = getUv2Offset();
        long normalOffset = getNormalOffset();

        MemoryUtil.memPutFloat(vertex + posOffset + 0L, pX);
        MemoryUtil.memPutFloat(vertex + posOffset + 4L, pY);
        MemoryUtil.memPutFloat(vertex + posOffset + 8L, pZ);

        if (colorOffset != -1) {
            putRgba(vertex + colorOffset, pColor);
        }

        if (uv0Offset != -1) {
            MemoryUtil.memPutFloat(vertex + uv0Offset + 0L, pU);
            MemoryUtil.memPutFloat(vertex + uv0Offset + 4L, pV);
        }

        if (uv1Offset != -1) {
            putPackedUv(vertex + uv1Offset, pPackedOverlay);
        }

        if (uv2Offset != -1) {
            putPackedUv(vertex + uv2Offset, pPackedLight);
        }

        if (normalOffset != -1) {
            ByteBufferUtils.putNormal(vertex + normalOffset + 0L, pNormalX);
            ByteBufferUtils.putNormal(vertex + normalOffset + 1L, pNormalY);
            ByteBufferUtils.putNormal(vertex + normalOffset + 2L, pNormalZ);
        }
    }

    public IClientBuffer getBuffer() {
        return buffer;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public RenderType getKey() {
        return key;
    }

    public int getOffset() {
        return offset;
    }

    public static MeshCollector create(RenderType key, IClientBuffer buffer, int offset) {
        return LE ?
                new LE(key, buffer, offset) :
                new BE(key, buffer, offset);
    }

    public static class BE extends MeshCollector {

        private BE(
                RenderType key,
                IClientBuffer buffer,
                int offset
        ) {
            super(
                    key,
                    buffer,
                    offset
            );
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

    public static class LE extends MeshCollector {

        private LE(
                RenderType key,
                IClientBuffer buffer,
                int offset
        ) {
            super(
                    key,
                    buffer,
                    offset
            );
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

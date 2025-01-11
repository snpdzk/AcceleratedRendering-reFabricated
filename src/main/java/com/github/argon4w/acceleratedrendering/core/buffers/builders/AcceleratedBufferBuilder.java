package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.IAcceleratedBuffers;
import com.github.argon4w.acceleratedrendering.core.utils.ByteBufferUtils;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

public abstract class AcceleratedBufferBuilder implements VertexConsumer, IVertexConsumerExtension {

    private static final boolean LE = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

    private final IAcceleratedBuffers buffers;
    private final RenderType renderType;
    private final VertexFormat vertexFormat;
    private final VertexFormat.Mode mode;

    private int polygonVertexCount;
    private int vertexCount;
    private long vertex;
    private long varying;
    private int pose;

    public AcceleratedBufferBuilder(IAcceleratedBuffers buffers, RenderType renderType) {
        this.buffers = buffers;
        this.renderType = renderType;
        this.vertexFormat = renderType.format;
        this.mode = renderType.mode;

        this.polygonVertexCount = 0;
        this.vertexCount = 0;
        this.vertex = -1;
        this.varying = -1;
        this.pose = -1;
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

    @Override
    public VertexConsumer addVertex(float pX, float pY, float pZ) {
        vertexCount++;

        if (++ polygonVertexCount >= mode.primitiveLength) {
            buffers.reserveIndices(mode.primitiveLength);
            polygonVertexCount = 0;
        }

        vertex = buffers.reserveVertex();
        long offset = getPosOffset();

        MemoryUtil.memPutFloat(vertex + offset + 0L, pX);
        MemoryUtil.memPutFloat(vertex + offset + 4L, pY);
        MemoryUtil.memPutFloat(vertex + offset + 8L, pZ);

        varying = buffers.reserveVarying();
        MemoryUtil.memPutInt(varying + 0 * 4L, -1);
        MemoryUtil.memPutInt(varying + 1 * 4L, -1);

        return this;
    }

    @Override
    public VertexConsumer setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
        long offset = getColorOffset();

        if (offset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutByte(vertex + offset + 0L, (byte)pRed);
        MemoryUtil.memPutByte(vertex + offset + 1L, (byte)pGreen);
        MemoryUtil.memPutByte(vertex + offset + 2L, (byte)pBlue);
        MemoryUtil.memPutByte(vertex + offset + 3L, (byte)pAlpha);

        MemoryUtil.memPutByte(varying + 1 * 4L + 0L, (byte) pRed);
        MemoryUtil.memPutByte(varying + 1 * 4L + 1L, (byte) pGreen);
        MemoryUtil.memPutByte(varying + 1 * 4L + 2L, (byte) pBlue);
        MemoryUtil.memPutByte(varying + 1 * 4L + 3L, (byte) pAlpha);

        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        long offset = getUvOffset();

        if (offset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutFloat(vertex + offset + 0L, pU);
        MemoryUtil.memPutFloat(vertex + offset + 4L, pV);

        return this;
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        long offset = getUv1Offset();

        if (offset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutShort(vertex + offset + 0L, (short) pU);
        MemoryUtil.memPutShort(vertex + offset + 2L, (short) pV);

        MemoryUtil.memPutShort(varying + 3 * 4L + 0L, (short) pU);
        MemoryUtil.memPutShort(varying + 3 * 4L + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        long offset = getUv2Offset();

        if (offset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutShort(vertex + offset + 0L, (short) pU);
        MemoryUtil.memPutShort(vertex + offset + 2L, (short) pV);

        MemoryUtil.memPutShort(varying + 2 * 4L + 0L, (short) pU);
        MemoryUtil.memPutShort(varying + 2 * 4L + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setNormal(float pNormalX, float pNormalY, float pNormalZ) {
        long offset = getNormalOffset();

        if (offset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        ByteBufferUtils.putNormal(vertex + offset + 0L, pNormalX);
        ByteBufferUtils.putNormal(vertex + offset + 1L, pNormalY);
        ByteBufferUtils.putNormal(vertex + offset + 2L, pNormalZ);

        return this;
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
        vertexCount++;

        if (++ polygonVertexCount >= mode.primitiveLength) {
            buffers.reserveIndices(mode.primitiveLength);
            polygonVertexCount = 0;
        }

        long vertex = buffers.reserveVertex();
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

        long varying = buffers.reserveVarying();
        MemoryUtil.memPutInt(varying + 0 * 4L, -1);
        MemoryUtil.memPutInt(varying + 1 * 4L, -1);
        MemoryUtil.memPutInt(varying + 2 * 4L, -1);
        MemoryUtil.memPutInt(varying + 3 * 4L, -1);
        MemoryUtil.memPutInt(varying + 4 * 4L, -1);
    }

    @Override
    public void beginTransform(PoseStack.Pose pose) {
        this.pose = buffers.getPose();

        long transform = buffers.reservePose();
        long normal = transform + 4 * 4 * 4;

        ByteBufferUtils.putMatrix4f(transform, pose.pose());
        ByteBufferUtils.putMatrix3x4f(normal, pose.normal());
    }

    @Override
    public void endTransform() {
        this.pose = -1;
    }

    @Override
    public void addClientMesh(RenderType renderType, ByteBuffer vertexBuffer, int size, int color, int light, int overlay) {
        if (!this.renderType.equals(renderType)) {
            throw new IllegalArgumentException("Incorrect RenderType: " + renderType.toString());
        }

        buffers.reserveIndices(size);
        vertexCount += size;

        long vertex = buffers.reserveVertices(size);

        ByteBufferUtils.putByteBuffer(vertexBuffer, vertex, (long) size * getSize());
        addClientVaryings(size, color, light, overlay);
    }

    @Override
    public void addServerMesh(RenderType renderType, int offset, int size, int color, int light, int overlay) {
        if (!this.renderType.equals(renderType)) {
            throw new IllegalArgumentException("Incorrect RenderType: " + renderType.toString());
        }

        buffers.reserveIndices(size);
        buffers.reserveVertices(size);
        vertexCount += size;

        addServerVaryings(size, offset / getSize(), color, light, overlay);
    }

    @Override
    public boolean supportAcceleratedRendering() {
        return true;
    }

    @Override
    public Set<RenderType> getRenderTypes() {
        return Set.of(renderType);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void addServerVaryings(int size, int mesh, int color, int light, int overlay) {
        long varying = buffers.reserveVaryings(size);

        for (int i = 0; i < size; i++) {
            MemoryUtil.memPutInt(varying + i * 5L * 4L + 0 * 4L, mesh + i);
            MemoryUtil.memPutInt(varying + i * 5L * 4L + 1 * 4L, pose);
            putRgba(varying + i * 5L * 4L + 2 * 4L, color);
            putPackedUv(varying + i * 5L * 4L + 3 * 4L, light);
            putPackedUv(varying + i * 5L * 4L + 4 * 4L, overlay);
        }
    }

    private void addClientVaryings(int size, int color, int light, int overlay) {
        long varying = buffers.reserveVaryings(size);

        for (int i = 0; i < size; i++) {
            MemoryUtil.memPutInt(varying + i * 5L * 4L + 0 * 4L, -1);
            MemoryUtil.memPutInt(varying + i * 5L * 4L + 1 * 4L, pose);
            putRgba(varying + i * 5L * 4L + 2 * 4L, color);
            putPackedUv(varying + i * 5L * 4L + 3 * 4L, light);
            putPackedUv(varying + i * 5L * 4L + 4 * 4L, overlay);
        }
    }

    public static AcceleratedBufferBuilder create(IAcceleratedBuffers buffers, RenderType renderType) {
        return LE
                ? new LE(buffers, renderType)
                : new BE(buffers, renderType);
    }

    public static class BE extends AcceleratedBufferBuilder {

        private BE(IAcceleratedBuffers buffers, RenderType renderType) {
            super(buffers, renderType);
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

    public static class LE extends AcceleratedBufferBuilder {

        private LE(IAcceleratedBuffers buffers, RenderType renderType) {
            super(buffers, renderType);
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

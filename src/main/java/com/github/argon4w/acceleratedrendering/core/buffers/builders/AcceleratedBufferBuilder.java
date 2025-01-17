package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.IAcceleratedBuffers;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.github.argon4w.acceleratedrendering.features.culling.NormalCullingFeature;
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
    private final IBufferEnvironment bufferEnvironment;
    private final RenderType renderType;
    private final VertexFormat.Mode mode;

    private int polygonVertexCount;
    private int vertexCount;
    private long vertex;
    private long varying;
    private int sharing;

    public AcceleratedBufferBuilder(
            IAcceleratedBuffers buffers,
            IBufferEnvironment bufferEnvironment,
            RenderType renderType

    ) {
        this.buffers = buffers;
        this.bufferEnvironment = bufferEnvironment;
        this.renderType = renderType;
        this.mode = renderType.mode;

        this.polygonVertexCount = 0;
        this.vertexCount = 0;
        this.vertex = -1;
        this.varying = -1;
        this.sharing = -1;
    }

    public abstract void putRgba(long pointer, int color);
    public abstract void putPackedUv(long pointer, int packedUv);

    private int getSize() {
        return bufferEnvironment.getVertexSize();
    }

    private long getPosOffset() {
        return bufferEnvironment.getOffset(VertexFormatElement.POSITION);
    }

    private long getColorOffset() {
        return bufferEnvironment.getOffset(VertexFormatElement.COLOR);
    }

    private long getUvOffset() {
        return bufferEnvironment.getOffset(VertexFormatElement.UV);
    }

    private long getUv1Offset() {
        return bufferEnvironment.getOffset(VertexFormatElement.UV1);
    }

    public long getUv2Offset() {
        return bufferEnvironment.getOffset(VertexFormatElement.UV2);
    }

    public long getNormalOffset() {
        return bufferEnvironment.getOffset(VertexFormatElement.NORMAL);
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
        MemoryUtil.memPutInt(varying + 2 * 4L, pColor);
        MemoryUtil.memPutInt(varying + 3 * 4L, pPackedLight);
        MemoryUtil.memPutInt(varying + 4 * 4L, pPackedOverlay);
    }

    @Override
    public void beginTransform(PoseStack.Pose pose) {
        this.sharing = buffers.getSharing();

        long transform = buffers.reserveSharings();
        long normal = transform + 4L * 4L * 4L;
        long flags = normal + 4L * 3L * 4L;

        ByteBufferUtils.putMatrix4f(transform, pose.pose());
        ByteBufferUtils.putMatrix3x4f(normal, pose.normal());
        MemoryUtil.memPutFloat(flags, bufferEnvironment.getSharingFlags());
    }

    @Override
    public void endTransform() {
        this.sharing = -1;
    }

    @Override
    public void addClientMesh(RenderType renderType, ByteBuffer vertexBuffer, int size, int color, int light, int overlay) {
        if (!this.renderType.equals(renderType)) {
            throw new IllegalArgumentException("Incorrect RenderType: " + renderType.toString());
        }

        buffers.reserveIndices(size);
        vertexCount += size;

        long vertex = buffers.reserveVertices(size);
        long varying = buffers.reserveVaryings(size);

        ByteBufferUtils.putByteBuffer(vertexBuffer, vertex, (long) size * getSize());

        for (int i = 0; i < size; i++) {
            long address = varying + i * 5L * 4L;
            MemoryUtil.memPutInt( address + 0 * 4L, -1);
            MemoryUtil.memPutInt(address + 1 * 4L, sharing);
            putRgba(address + 2 * 4L, color);
            putPackedUv(address + 3 * 4L, light);
            putPackedUv(address + 4 * 4L, overlay);
        }
    }

    @Override
    public void addServerMesh(RenderType renderType, int offset, int size, int color, int light, int overlay) {
        if (!this.renderType.equals(renderType)) {
            throw new IllegalArgumentException("Incorrect RenderType: " + renderType.toString());
        }

        buffers.reserveIndices(size);
        buffers.reserveVertices(size);
        vertexCount += size;

        int mesh = offset / getSize();
        long varying = buffers.reserveVaryings(size);

        for (int i = 0; i < size; i++) {
            long address = varying + i * 5L * 4L;
            MemoryUtil.memPutInt( address + 0 * 4L, mesh + i);
            MemoryUtil.memPutInt(address + 1 * 4L, sharing);
            putRgba(address + 2 * 4L, color);
            putPackedUv(address + 3 * 4L, light);
            putPackedUv(address + 4 * 4L, overlay);
        }
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

    @Override
    public IBufferEnvironment getBufferEnvironment() {
        return bufferEnvironment;
    }

    public static AcceleratedBufferBuilder create(
            IAcceleratedBuffers buffers,
            IBufferEnvironment bufferEnvironment,
            RenderType renderType
    ) {
        return LE
                ? new LE(buffers, bufferEnvironment, renderType)
                : new BE(buffers, bufferEnvironment, renderType);
    }

    public static class BE extends AcceleratedBufferBuilder {

        private BE(
                IAcceleratedBuffers buffers,
                IBufferEnvironment bufferEnvironment,
                RenderType renderType
        ) {
            super(
                    buffers,
                    bufferEnvironment,
                    renderType
            );
        }

        @Override
        public void putRgba(long pointer, int color) {
            MemoryUtil.memPutInt(
                    pointer,
                    Integer.reverseBytes(FastColor.ABGR32.fromArgb32(color))
            );
        }

        @Override
        public void putPackedUv(long pointer, int packed) {
            MemoryUtil.memPutShort(pointer, (short) (packed & 65535));
            MemoryUtil.memPutShort(pointer + 2L, (short) (packed >> 16 & 65535));
        }
    }

    public static class LE extends AcceleratedBufferBuilder {

        private LE(
                IAcceleratedBuffers buffers,
                IBufferEnvironment bufferEnvironment,
                RenderType renderType
        ) {
            super(
                    buffers,
                    bufferEnvironment,
                    renderType
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

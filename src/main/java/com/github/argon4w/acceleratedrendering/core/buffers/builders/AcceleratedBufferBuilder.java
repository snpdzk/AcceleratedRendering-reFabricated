package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSetPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.utils.ByteBufferUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

public abstract class AcceleratedBufferBuilder implements VertexConsumer, IVertexConsumerExtension {

    private static final boolean LE = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

    private final ElementBuffer elementBuffer;
    private final IBufferEnvironment bufferEnvironment;
    private final AcceleratedBufferSetPool.BufferSet bufferSet;
    private final RenderType renderType;
    private final VertexFormat.Mode mode;

    private PoseStack.Pose pose;
    private int elementCount;
    private int vertexCount;
    private long vertex;
    private long varying;
    private long transform;
    private int sharing;

    public AcceleratedBufferBuilder(
            ElementBuffer elementBuffer,
            IBufferEnvironment bufferEnvironment,
            AcceleratedBufferSetPool.BufferSet bufferSet,
            RenderType renderType

    ) {
        this.elementBuffer = elementBuffer;
        this.bufferEnvironment = bufferEnvironment;
        this.bufferSet = bufferSet;
        this.renderType = renderType;
        this.mode = renderType.mode;

        this.pose = null;
        this.elementCount = 0;
        this.vertexCount = 0;
        this.vertex = -1;
        this.varying = -1;
        this.transform = -1;
        this.sharing = -1;
    }

    public abstract void putRgba(long pointer, int color);
    public abstract void putPackedUv(long pointer, int packedUv);

    public void checkPolygon() {
        if (++ elementCount < mode.primitiveLength) {
            return;
        }

        elementBuffer.reserveElements(mode.primitiveLength);
        elementCount = 0;
    }

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
    public VertexConsumer addVertex(PoseStack.Pose pPose, float pX, float pY, float pZ) {
        beginTransform(pPose);
        return addVertex(pX, pY, pZ);
    }

    @Override
    public VertexConsumer addVertex(float pX, float pY, float pZ) {
        vertexCount ++;
        checkPolygon();

        vertex = bufferSet.reserveVertex();
        long offset = getPosOffset();

        MemoryUtil.memPutFloat(vertex + offset + 0L, pX);
        MemoryUtil.memPutFloat(vertex + offset + 4L, pY);
        MemoryUtil.memPutFloat(vertex + offset + 8L, pZ);

        varying = bufferSet.reserveVarying();
        MemoryUtil.memPutInt(varying + 0 * 4L, -1);
        MemoryUtil.memPutInt(varying + 1 * 4L, sharing);

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

        MemoryUtil.memPutByte(varying + 2 * 4L + 0L, (byte) pRed);
        MemoryUtil.memPutByte(varying + 2 * 4L + 1L, (byte) pGreen);
        MemoryUtil.memPutByte(varying + 2 * 4L + 2L, (byte) pBlue);
        MemoryUtil.memPutByte(varying + 2 * 4L + 3L, (byte) pAlpha);

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

        MemoryUtil.memPutShort(varying + 4 * 4L + 0L, (short) pU);
        MemoryUtil.memPutShort(varying + 4 * 4L + 2L, (short) pV);

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

        //MemoryUtil.memPutShort(vertex + offset + 0L, (short) pU);
        //MemoryUtil.memPutShort(vertex + offset + 2L, (short) pV);

        MemoryUtil.memPutShort(varying + 3 * 4L + 0L, (short) pU);
        MemoryUtil.memPutShort(varying + 3 * 4L + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setNormal(PoseStack.Pose pPose, float pNormalX, float pNormalY, float pNormalZ) {
        if (transform == -1) {
            return VertexConsumer.super.setNormal(
                    pPose,
                    pNormalX,
                    pNormalY,
                    pNormalZ
            );
        }

        if (this.pose != pPose) {
            ByteBufferUtils.putMatrix3x4f(transform + 4L * 4L * 4L, pose.normal());
        }

        return setNormal(
                pNormalX,
                pNormalY,
                pNormalZ
        );
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
        checkPolygon();

        long vertex = bufferSet.reserveVertex();
        long posOffset = getPosOffset();
        long uv0Offset = getUvOffset();
        long normalOffset = getNormalOffset();

        MemoryUtil.memPutFloat(vertex + posOffset + 0L, pX);
        MemoryUtil.memPutFloat(vertex + posOffset + 4L, pY);
        MemoryUtil.memPutFloat(vertex + posOffset + 8L, pZ);

        if (uv0Offset != -1) {
            MemoryUtil.memPutFloat(vertex + uv0Offset + 0L, pU);
            MemoryUtil.memPutFloat(vertex + uv0Offset + 4L, pV);
        }

        if (normalOffset != -1) {
            ByteBufferUtils.putNormal(vertex + normalOffset + 0L, pNormalX);
            ByteBufferUtils.putNormal(vertex + normalOffset + 1L, pNormalY);
            ByteBufferUtils.putNormal(vertex + normalOffset + 2L, pNormalZ);
        }

        long varying = bufferSet.reserveVarying();

        MemoryUtil.memPutInt(varying + 0 * 4L, -1);
        MemoryUtil.memPutInt(varying + 1 * 4L, -1);
        putRgba(varying + 2 * 4L, pColor);
        putPackedUv(varying + 3 * 4L, pPackedLight);
        putPackedUv(varying + 4 * 4L, pPackedOverlay);
    }

    @Override
    public void beginTransform(PoseStack.Pose pose) {
        if (this.pose == pose) {
            return;
        }

        this.pose = pose;
        this.sharing = bufferSet.getSharing();
        this.transform = bufferSet.reserveSharing();

        long normal = transform + 4L * 4L * 4L;
        long flags = normal + 4L * 3L * 4L;

        ByteBufferUtils.putMatrix4f(transform, pose.pose());
        ByteBufferUtils.putMatrix3x4f(normal, pose.normal());
        MemoryUtil.memPutInt(flags, bufferEnvironment.getSharingFlags());
    }

    @Override
    public void endTransform() {
        this.pose = null;
        this.sharing = -1;
        this.transform = -1;
    }

    @Override
    public void addClientMesh(
            RenderType renderType,
            ByteBuffer vertexBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        elementBuffer.reserveElements(size);
        vertexCount += size;

        long vertex = bufferSet.reservePolygons(size);
        long varying = bufferSet.reserveVaryings(size);

        ByteBufferUtils.putByteBuffer(vertexBuffer, vertex, (long) size * getSize());

        for (int i = 0; i < size; i++) {
            long address = varying + i * 5L * 4L;

            MemoryUtil.memPutInt(address + 0 * 4L, -1);
            MemoryUtil.memPutInt(address + 1 * 4L, sharing);
            putRgba(address + 2 * 4L, color);
            putPackedUv(address + 3 * 4L, light);
            putPackedUv(address + 4 * 4L, overlay);
        }
    }

    @Override
    public void addServerMesh(
            RenderType renderType,
            int offset,
            int size,
            int color,
            int light,
            int overlay
    ) {
        elementBuffer.reserveElements(size);
        bufferSet.reservePolygons(size);
        vertexCount += size;

        int mesh = offset / getSize();
        long varying = bufferSet.reserveVaryings(size);

        for (int i = 0; i < size; i++) {
            long address = varying + i * 5L * 4L;

            MemoryUtil.memPutInt(address + 0 * 4L, mesh + i);
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

    public ElementBuffer getElementBuffer() {
        return elementBuffer;
    }

    public static AcceleratedBufferBuilder create(
            ElementBuffer elementBuffer,
            IBufferEnvironment bufferEnvironment,
            AcceleratedBufferSetPool.BufferSet bufferSet,
            RenderType renderType
    ) {
        return LE
                ? new LE(elementBuffer, bufferEnvironment, bufferSet, renderType)
                : new BE(elementBuffer, bufferEnvironment, bufferSet, renderType);
    }

    public static class BE extends AcceleratedBufferBuilder {

        private BE(
                ElementBuffer elementBuffer,
                IBufferEnvironment bufferEnvironment,
                AcceleratedBufferSetPool.BufferSet bufferSet,
                RenderType renderType
        ) {
            super(
                    elementBuffer,
                    bufferEnvironment,
                    bufferSet,
                    renderType
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

    public static class LE extends AcceleratedBufferBuilder {

        private LE(
                ElementBuffer elementBuffer,
                IBufferEnvironment bufferEnvironment,
                AcceleratedBufferSetPool.BufferSet bufferSet,
                RenderType renderType
        ) {
            super(
                    elementBuffer,
                    bufferEnvironment,
                    bufferSet,
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

package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSetPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.ElementBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.MappedBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.VertexBufferPool;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IExtraVertexData;
import com.github.argon4w.acceleratedrendering.core.utils.ByteUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

public class AcceleratedBufferBuilder implements VertexConsumer, IAcceleratedVertexConsumer {

    private final VertexBufferPool.VertexBuffer vertexBuffer;
    private final MappedBufferPool.Pooled varyingBuffer;
    private final ElementBufferPool.ElementSegment elementSegment;

    private final AcceleratedBufferSetPool.BufferSet bufferSet;
    private final RenderType renderType;
    private final VertexFormat.Mode mode;
    private final int vertexSize;
    private final int polygonSize;
    private final int polygonElementCount;

    private final long posOffset;
    private final long colorOffset;
    private final long uv0Offset;
    private final long uv1Offset;
    private final long uv2Offset;
    private final long normalOffset;

    private int elementCount;
    private int vertexCount;
    private long vertex;
    private long transform;
    private long normal;
    private int sharing;
    private int cachedSharing;

    private Matrix4f cachedTransform;
    private Matrix3f cachedNormal;

    public AcceleratedBufferBuilder(
            VertexBufferPool.VertexBuffer vertexBuffer,
            MappedBufferPool.Pooled varyingBuffer,
            ElementBufferPool.ElementSegment elementSegment,
            AcceleratedBufferSetPool.BufferSet bufferSet,
            RenderType renderType
    ) {
        this.vertexBuffer = vertexBuffer;
        this.varyingBuffer = varyingBuffer;
        this.elementSegment = elementSegment;

        this.bufferSet = bufferSet;
        this.renderType = renderType;
        this.mode = this.renderType.mode;
        this.vertexSize = this.bufferSet.getVertexSize();
        this.polygonSize = this.mode.primitiveLength;
        this.polygonElementCount = this.mode.indexCount(this.polygonSize);

        this.posOffset = bufferSet.getOffset(VertexFormatElement.POSITION);
        this.colorOffset = bufferSet.getOffset(VertexFormatElement.COLOR);
        this.uv0Offset = bufferSet.getOffset(VertexFormatElement.UV0);
        this.uv1Offset = bufferSet.getOffset(VertexFormatElement.UV1);
        this.uv2Offset = bufferSet.getOffset(VertexFormatElement.UV2);
        this.normalOffset = bufferSet.getOffset(VertexFormatElement.NORMAL);

        this.elementCount = 0;
        this.vertexCount = 0;
        this.vertex = -1;
        this.transform = -1;
        this.normal = -1;
        this.sharing = -1;
        this.cachedSharing = -1;

        this.cachedTransform = null;
        this.cachedNormal = null;
    }

    @Override
    public VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ
    ) {
        beginTransform(pPose.pose(), pPose.normal());
        return addVertex(
                pX,
                pY,
                pZ
        );
    }

    @Override
    public VertexConsumer addVertex(
            float pX,
            float pY,
            float pZ
    ) {
        long vertex = vertexBuffer.reserve(vertexSize);
        long varying = varyingBuffer.reserve(4L * 4L);

        this.vertex = vertex;

        MemoryUtil.memPutFloat(vertex + posOffset + 0L, pX);
        MemoryUtil.memPutFloat(vertex + posOffset + 4L, pY);
        MemoryUtil.memPutFloat(vertex + posOffset + 8L, pZ);

        MemoryUtil.memPutInt(varying + 0L * 4L, 0);
        MemoryUtil.memPutInt(varying + 1L * 4L, sharing);
        MemoryUtil.memPutInt(varying + 2L * 4L, -1);
        MemoryUtil.memPutInt(varying + 3L * 4L, bufferSet.getFlags(mode));

        IExtraVertexData data = bufferSet.getExtraVertex(mode);
        data.addExtraVertex(vertex);
        data.addExtraVarying(varying);

        vertexCount ++;
        elementCount ++;

        if (elementCount >= polygonSize) {
            elementSegment.countPolygons(polygonElementCount);
            elementCount = 0;
            sharing = -1;
        }

        return this;
    }

    @Override
    public VertexConsumer setColor(
            int pRed,
            int pGreen,
            int pBlue,
            int pAlpha
    ) {
        if (colorOffset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutByte(vertex + colorOffset + 0L, (byte) pRed);
        MemoryUtil.memPutByte(vertex + colorOffset + 1L, (byte) pGreen);
        MemoryUtil.memPutByte(vertex + colorOffset + 2L, (byte) pBlue);
        MemoryUtil.memPutByte(vertex + colorOffset + 3L, (byte) pAlpha);

        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        if (uv0Offset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutFloat(vertex + uv0Offset + 0L, pU);
        MemoryUtil.memPutFloat(vertex + uv0Offset + 4L, pV);

        return this;
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        if (uv1Offset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutShort(vertex + uv1Offset + 0L, (short) pU);
        MemoryUtil.memPutShort(vertex + uv1Offset + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        if (uv2Offset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutShort(vertex + uv2Offset + 0L, (short) pU);
        MemoryUtil.memPutShort(vertex + uv2Offset + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setNormal(
            PoseStack.Pose pPose,
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        Matrix3f normalMatrix = pPose.normal();

        if (sharing == -1) {
            return VertexConsumer.super.setNormal(
                    pPose,
                    pNormalX,
                    pNormalY,
                    pNormalZ
            );
        }

        if (!normalMatrix.equals(cachedNormal)) {
            ByteUtils.putMatrix3x4f(normal, normalMatrix);
        }

        return setNormal(
                pNormalX,
                pNormalY,
                pNormalZ
        );
    }

    @Override
    public VertexConsumer setNormal(
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        if (normalOffset == -1) {
            return this;
        }

        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        ByteUtils.putNormal(vertex + normalOffset + 0L, pNormalX);
        ByteUtils.putNormal(vertex + normalOffset + 1L, pNormalY);
        ByteUtils.putNormal(vertex + normalOffset + 2L, pNormalZ);

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
        long vertex = vertexBuffer.reserve(vertexSize);
        long varying = varyingBuffer.reserve(4L * 4L);
        IExtraVertexData data = bufferSet.getExtraVertex(mode);

        data.addExtraVertex(vertex);
        data.addExtraVarying(varying);

        MemoryUtil.memPutFloat(vertex + posOffset + 0L, pX);
        MemoryUtil.memPutFloat(vertex + posOffset + 4L, pY);
        MemoryUtil.memPutFloat(vertex + posOffset + 8L, pZ);

        if (colorOffset != -1) {
            MemoryUtil.memPutInt(vertex + colorOffset + 0L, FastColor.ABGR32.fromArgb32(pColor));
        }

        if (uv0Offset != -1) {
            MemoryUtil.memPutFloat(vertex + uv0Offset + 0L, pU);
            MemoryUtil.memPutFloat(vertex + uv0Offset + 4L, pV);
        }

        if (uv1Offset != -1) {
            MemoryUtil.memPutInt(vertex + uv1Offset + 0L, pPackedOverlay);
        }

        if (uv2Offset != -1) {
            MemoryUtil.memPutInt(vertex + uv2Offset + 0L, pPackedLight);
        }

        if (normalOffset != -1) {
            ByteUtils.putNormal(vertex + normalOffset + 0L, pNormalX);
            ByteUtils.putNormal(vertex + normalOffset + 1L, pNormalY);
            ByteUtils.putNormal(vertex + normalOffset + 2L, pNormalZ);
        }

        MemoryUtil.memPutInt(varying + 0L * 4L, 0);
        MemoryUtil.memPutInt(varying + 1L * 4L, sharing);
        MemoryUtil.memPutInt(varying + 2L * 4L, -1);
        MemoryUtil.memPutInt(varying + 3L * 4L, bufferSet.getFlags(mode));

        vertexCount ++;
        elementCount ++;

        if (elementCount >= polygonSize) {
            elementSegment.countPolygons(polygonElementCount);
            elementCount = 0;
            sharing = -1;
        }
    }

    @Override
    public void beginTransform(Matrix4f transformMatrix, Matrix3f normalMatrix) {
        if (CoreFeature.shouldCacheSamePose()
                && transformMatrix.equals(cachedTransform)
                && normalMatrix.equals(cachedNormal)
        ) {
            sharing = cachedSharing;
            return;
        }

        cachedTransform = new Matrix4f(transformMatrix);
        cachedNormal = new Matrix3f(normalMatrix);

        sharing = bufferSet.getSharing();
        cachedSharing = sharing;

        transform = bufferSet.reserveSharing();
        normal = transform + 4L * 4L * 4L;

        ByteUtils.putMatrix4f(transform, transformMatrix);
        ByteUtils.putMatrix3x4f(normal, normalMatrix);
    }

    @Override
    public void endTransform() {
        cachedTransform = null;
        cachedNormal = null;
        sharing = -1;
        cachedSharing = -1;
    }

    @Override
    public void addClientMesh(
            RenderType renderType,
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        elementSegment.countPolygons(mode.indexCount(size));
        vertexCount += size;

        long vertex = vertexBuffer.reserve(vertexSize * (long) size);
        long varying = varyingBuffer.reserve(4L * 4L * size);
        long length = (long) size * bufferSet.getVertexSize();
        IExtraVertexData data = bufferSet.getExtraVertex(mode);

        data.addExtraVertex(vertex);
        data.addExtraVarying(varying);

        ByteUtils.putByteBuffer(
                meshBuffer,
                vertex,
                length
        );

        if (colorOffset != -1) {
            MemoryUtil.memPutInt(vertex + colorOffset, FastColor.ABGR32.fromArgb32(color));
        }

        if (uv1Offset != -1) {
            MemoryUtil.memPutInt(vertex + uv1Offset, overlay);
        }

        if (uv2Offset != -1) {
            MemoryUtil.memPutInt(vertex + uv2Offset, light);
        }

        MemoryUtil.memPutInt(varying + 1L * 4L, sharing);
        MemoryUtil.memPutInt(varying + 2L * 4L, -1);
        MemoryUtil.memPutInt(varying + 3L * 4L, bufferSet.getFlags(mode));

        for (int i = 0; i < size; i++) {
            MemoryUtil.memPutInt(varying + i * 4L * 4L, i);
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
        elementSegment.countPolygons(mode.indexCount(size));
        vertexCount += size;

        int mesh = offset / bufferSet.getVertexSize();
        long vertex = vertexBuffer.reserve(vertexSize * (long) size);
        long varying = varyingBuffer.reserve(4L * 4L * size);
        IExtraVertexData data = bufferSet.getExtraVertex(mode);

        data.addExtraVertex(vertex);
        data.addExtraVarying(varying);

        if (colorOffset != -1) {
            MemoryUtil.memPutInt(vertex + colorOffset, FastColor.ABGR32.fromArgb32(color));
        }

        if (uv1Offset != -1) {
            MemoryUtil.memPutInt(vertex + uv1Offset, overlay);
        }

        if (uv2Offset != -1) {
            MemoryUtil.memPutInt(vertex + uv2Offset, light);
        }

        MemoryUtil.memPutInt(varying + 1L * 4L, sharing);
        MemoryUtil.memPutInt(varying + 2L * 4L, mesh);
        MemoryUtil.memPutInt(varying + 3L * 4L, bufferSet.getFlags(mode));

        for (int i = 0; i < size; i++) {
            MemoryUtil.memPutInt(varying + i * 4L * 4L, i);
        }
    }

    @Override
    public boolean isAccelerated() {
        return true;
    }

    @Override
    public void mapRenderTypes(Map<RenderType, VertexConsumer> map) {
        map.put(renderType, this);
    }

    @Override
    public Set<RenderType> getRenderTypes() {
        return Set.of(renderType);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVertexOffset() {
        return vertexBuffer.getOffset();
    }

    public VertexBufferPool.VertexBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public MappedBufferPool.Pooled getVaryingBuffer() {
        return varyingBuffer;
    }

    public ElementBufferPool.ElementSegment getElementSegment() {
        return elementSegment;
    }
}

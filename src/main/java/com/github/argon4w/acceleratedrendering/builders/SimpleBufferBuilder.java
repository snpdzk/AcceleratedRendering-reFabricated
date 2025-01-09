package com.github.argon4w.acceleratedrendering.builders;

import com.github.argon4w.acceleratedrendering.buffers.IVertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.buffers.IEntityBuffers;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.github.argon4w.acceleratedrendering.utils.ByteBufferUtils.*;

public abstract class SimpleBufferBuilder implements VertexConsumer, IVertexConsumerExtension {

    private static final boolean LE = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);
    private static final VertexFormat FORMAT = DefaultVertexFormat.NEW_ENTITY;
    private static final int SIZE = FORMAT.getVertexSize();
    private static final VertexFormat.Mode MODE = VertexFormat.Mode.QUADS;

    private final IEntityBuffers bufferSet;
    private final RenderType renderType;
    
    private int vertices;
    private long vertex;
    private long varying;
    private int pose;

    public SimpleBufferBuilder(IEntityBuffers bufferSet, RenderType renderType) {
        this.bufferSet = bufferSet;
        this.renderType = renderType;

        this.vertices = 0;
        this.vertex = -1;
        this.varying = -1;
        this.pose = -1;
    }

    public abstract void putRgba(long pointer, int color);
    public abstract void putPackedUv(long pointer, int packedUv);

    @Override
    public MeshData sme$build() {
        if (vertices == 0) {
            return null;
        }
        
        MeshData meshData = new MeshData(null, new MeshData.DrawState(
                FORMAT,
                vertices,
                MODE.indexCount(vertices),
                MODE,
                VertexFormat.IndexType.INT
        ));
        meshData.indexBuffer = bufferSet.buildIndexBuffer(vertices);

        return meshData;
    }

    @Override
    public VertexConsumer addVertex(float pX, float pY, float pZ) {
        bufferSet.reserveIndex();
        vertices ++;

        vertex = bufferSet.reserveVertex();

        putFloat(vertex + 0L, pX);
        putFloat(vertex + 4L, pY);
        putFloat(vertex + 8L, pZ);

        varying = bufferSet.reserveVarying();
        putInt(varying + 0 * 4L, -1);

        return this;
    }

    @Override
    public VertexConsumer setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        putByte(vertex + 12L + 0L, (byte)pRed);
        putByte(vertex + 12L + 1L, (byte)pGreen);
        putByte(vertex + 12L + 2L, (byte)pBlue);
        putByte(vertex + 12L + 3L, (byte)pAlpha);

        putByte(varying + 1 * 4L + 0L, (byte) pRed);
        putByte(varying + 1 * 4L + 1L, (byte) pGreen);
        putByte(varying + 1 * 4L + 2L, (byte) pBlue);
        putByte(varying + 1 * 4L + 3L, (byte) pAlpha);

        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        putFloat(vertex + 16L + 0L, pU);
        putFloat(vertex + 16L + 4L, pV);

        return this;
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        putShort(vertex + 24L + 0L, (short) pU);
        putShort(vertex + 24L + 2L, (short) pV);

        putShort(varying + 3 * 4L + 0L, (short) pU);
        putShort(varying + 3 * 4L + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        putShort(vertex + 28L + 0L, (short) pU);
        putShort(vertex + 28L + 2L, (short) pV);

        putShort(varying + 2 * 4L + 0L, (short) pU);
        putShort(varying + 2 * 4L + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setNormal(float pNormalX, float pNormalY, float pNormalZ) {
        if (vertex == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        putNormalizedFloatToByte(vertex + 32L + 0L, pNormalX);
        putNormalizedFloatToByte(vertex + 32L + 1L, pNormalY);
        putNormalizedFloatToByte(vertex + 32L + 2L, pNormalZ);

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
        bufferSet.reserveIndex();
        this.vertices++;

        long vertex = bufferSet.reserveVertex();

        putFloat(vertex + 0L, pX);
        putFloat(vertex + 4L, pY);
        putFloat(vertex + 8L, pZ);

        putRgba(vertex + 12L, pColor);

        putFloat(vertex + 16L, pU);
        putFloat(vertex + 20L, pV);

        putPackedUv(vertex + 24L, pPackedOverlay);
        putPackedUv(vertex + 28L, pPackedLight);

        putNormalizedFloatToByte(vertex + 32L, pNormalX);
        putNormalizedFloatToByte(vertex + 33L, pNormalY);
        putNormalizedFloatToByte(vertex + 34L, pNormalZ);

        long varying = bufferSet.reserveVarying();
        putInt(varying + 0 * 4L, -1);
        putInt(varying + 1 * 4L, -1);
        putInt(varying + 2 * 4L, -1);
        putInt(varying + 3 * 4L, -1);
    }

    @Override
    public void sme$beginTransform(PoseStack.Pose pose) {
        this.pose = bufferSet.getPose();

        long transform = bufferSet.reservePose();
        long normal = transform + 4 * 4 * 4;

        putMatrix4f(transform, pose.pose());
        putMatrix3x4f(normal, pose.normal());
    }

    @Override
    public void sme$addMesh(ByteBuffer vertexBuffer, int count, int color, int light, int overlay) {
        vertices += count;
        
        long vertex = bufferSet.reserveVertices(count);
        long varying = bufferSet.reserveVaryings(count);

        copyToAddress(vertexBuffer, vertex, (long) count * SIZE);

        for (int i = 0; i < count; i++) {
            putInt(varying + i * 4L * 4L + 0 * 4L, pose);
            putRgba(varying + i * 4L * 4L + 1 * 4L, color);
            putPackedUv(varying + i * 4L * 4L + 2 * 4L, light);
            putPackedUv(varying + i * 4L * 4L + 3 * 4L, overlay);

            bufferSet.reserveIndex();
        }
    }

    @Override
    public boolean sme$supportAcceleratedRendering() {
        return true;
    }

    @Override
    public RenderType sme$getRenderType() {
        return renderType;
    }

    public static SimpleBufferBuilder create(IEntityBuffers bufferSet, RenderType renderType) {
        return LE ? new LE(bufferSet, renderType) : new BE(bufferSet, renderType);
    }

    public static class BE extends SimpleBufferBuilder {

        private BE(IEntityBuffers bufferSet, RenderType renderType) {
            super(bufferSet, renderType);
        }

        @Override
        public void putRgba(long pointer, int color) {
            putInt(pointer, Integer.reverseBytes(FastColor.ABGR32.fromArgb32(color)));
        }

        @Override
        public void putPackedUv(long pointer, int packed) {
            putShort(pointer, (short) (packed & 65535));
            putShort(pointer + 2L, (short) (packed >> 16 & 65535));
        }
    }

    public static class LE extends SimpleBufferBuilder {

        private LE(IEntityBuffers bufferSet, RenderType renderType) {
            super(bufferSet, renderType);
        }

        @Override
        public void putRgba(long pointer, int color) {
            putInt(pointer, FastColor.ABGR32.fromArgb32(color));
        }

        @Override
        public void putPackedUv(long pointer, int packed) {
            putInt(pointer, packed);
        }
    }
}

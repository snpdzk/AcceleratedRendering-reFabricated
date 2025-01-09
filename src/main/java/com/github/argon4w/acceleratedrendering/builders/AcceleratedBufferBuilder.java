package com.github.argon4w.acceleratedrendering.builders;

import com.github.argon4w.acceleratedrendering.buffers.AcceleratedBuffers;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.github.argon4w.acceleratedrendering.utils.ByteBufferUtils.*;

public abstract class AcceleratedBufferBuilder implements VertexConsumer, IVertexConsumerExtension {

    private static final boolean LE = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);
    private static final VertexFormat FORMAT = DefaultVertexFormat.NEW_ENTITY;
    private static final int SIZE = FORMAT.getVertexSize();
    private static final VertexFormat.Mode MODE = VertexFormat.Mode.QUADS;

    private final AcceleratedBuffers buffers;
    private final RenderType renderType;
    
    private int vertices;
    private long vertex;
    private long varying;
    private int pose;

    public AcceleratedBufferBuilder(AcceleratedBuffers buffers, RenderType renderType) {
        this.buffers = buffers;
        this.renderType = renderType;

        this.vertices = 0;
        this.vertex = -1;
        this.varying = -1;
        this.pose = -1;
    }

    public abstract void putRgba(long pointer, int color);
    public abstract void putPackedUv(long pointer, int packedUv);

    @Override
    public VertexConsumer addVertex(float pX, float pY, float pZ) {
        buffers.reserveIndex();
        vertices ++;

        vertex = buffers.reserveVertex();

        putFloat(vertex + 0L, pX);
        putFloat(vertex + 4L, pY);
        putFloat(vertex + 8L, pZ);

        varying = buffers.reserveVarying();
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

        putNormal(vertex + 32L + 0L, pNormalX);
        putNormal(vertex + 32L + 1L, pNormalY);
        putNormal(vertex + 32L + 2L, pNormalZ);

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
        buffers.reserveIndex();
        this.vertices++;

        long vertex = buffers.reserveVertex();

        putFloat(vertex + 0L, pX);
        putFloat(vertex + 4L, pY);
        putFloat(vertex + 8L, pZ);

        putRgba(vertex + 12L, pColor);

        putFloat(vertex + 16L, pU);
        putFloat(vertex + 20L, pV);

        putPackedUv(vertex + 24L, pPackedOverlay);
        putPackedUv(vertex + 28L, pPackedLight);

        putNormal(vertex + 32L, pNormalX);
        putNormal(vertex + 33L, pNormalY);
        putNormal(vertex + 34L, pNormalZ);

        long varying = buffers.reserveVarying();
        putInt(varying + 0 * 4L, -1);
        putInt(varying + 1 * 4L, -1);
        putInt(varying + 2 * 4L, -1);
        putInt(varying + 3 * 4L, -1);
    }

    @Override
    public void acceleratedrendering$beginTransform(PoseStack.Pose pose) {
        this.pose = buffers.getPose();

        long transform = buffers.reservePose();
        long normal = transform + 4 * 4 * 4;

        putMatrix4f(transform, pose.pose());
        putMatrix3x4f(normal, pose.normal());
    }

    @Override
    public void acceleratedrendering$addMesh(ByteBuffer vertexBuffer, int count, int color, int light, int overlay) {
        vertices += count;
        
        long vertex = buffers.reserveVertices(count);
        long varying = buffers.reserveVaryings(count);

        putByteBuffer(vertexBuffer, vertex, (long) count * SIZE);

        for (int i = 0; i < count; i++) {
            putInt(varying + i * 4L * 4L + 0 * 4L, pose);
            putRgba(varying + i * 4L * 4L + 1 * 4L, color);
            putPackedUv(varying + i * 4L * 4L + 2 * 4L, light);
            putPackedUv(varying + i * 4L * 4L + 3 * 4L, overlay);

            buffers.reserveIndex();
        }
    }

    @Override
    public boolean acceleratedrendering$supportAcceleratedRendering() {
        return true;
    }

    @Override
    public RenderType acceleratedrendering$getRenderType() {
        return renderType;
    }

    public int getVertices() {
        return vertices;
    }

    public static AcceleratedBufferBuilder create(AcceleratedBuffers buffers, RenderType renderType) {
        return LE
                ? new LE(buffers, renderType)
                : new BE(buffers, renderType);
    }

    public static class BE extends AcceleratedBufferBuilder {

        private BE(AcceleratedBuffers buffers, RenderType renderType) {
            super(buffers, renderType);
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

    public static class LE extends AcceleratedBufferBuilder {

        private LE(AcceleratedBuffers buffers, RenderType renderType) {
            super(buffers, renderType);
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

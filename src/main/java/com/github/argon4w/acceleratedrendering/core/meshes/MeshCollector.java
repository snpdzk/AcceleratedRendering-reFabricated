package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.backends.buffers.IClientBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.ByteUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.util.FastColor;
import org.lwjgl.system.MemoryUtil;

public class MeshCollector {

    private final IMesh.Builder builder;
    private final IClientBuffer buffer;
    private final int offset;
    private final int vertexSize;

    private final long posOffset;
    private final long colorOffset;
    private final long uv0Offset;
    private final long uv2Offset;
    private final long normalOffset;

    private int vertexCount;

    public MeshCollector(
            IMesh.Builder builder,
            VertexFormat vertexFormat,
            IClientBuffer buffer,
            int offset
    ) {
        this.builder = builder;
        this.buffer = buffer;
        this.offset = offset;

        this.vertexSize = vertexFormat.getVertexSize();
        this.colorOffset = vertexFormat.getOffset(VertexFormatElement.COLOR);
        this.posOffset = vertexFormat.getOffset(VertexFormatElement.POSITION);
        this.uv0Offset = vertexFormat.getOffset(VertexFormatElement.UV);
        this.uv2Offset = vertexFormat.getOffset(VertexFormatElement.UV2);
        this.normalOffset = vertexFormat.getOffset(VertexFormatElement.NORMAL);

        this.vertexCount = 0;
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

        long vertex = this.buffer.reserve(vertexSize);

        MemoryUtil.memPutFloat(vertex + posOffset + 0L, pX);
        MemoryUtil.memPutFloat(vertex + posOffset + 4L, pY);
        MemoryUtil.memPutFloat(vertex + posOffset + 8L, pZ);

        if (colorOffset != -1L) {
            MemoryUtil.memPutInt(vertex + colorOffset + 0L, FastColor.ABGR32.fromArgb32(pColor));
        }

        if (uv0Offset != -1L) {
            MemoryUtil.memPutFloat(vertex + uv0Offset + 0L, pU);
            MemoryUtil.memPutFloat(vertex + uv0Offset + 4L, pV);
        }

        if (uv2Offset != -1L) {
            MemoryUtil.memPutInt(vertex + uv2Offset + 0L, pPackedLight);
        }

        if (normalOffset != -1L) {
            ByteUtils.putNormal(vertex + normalOffset + 0L, pNormalX);
            ByteUtils.putNormal(vertex + normalOffset + 1L, pNormalY);
            ByteUtils.putNormal(vertex + normalOffset + 2L, pNormalZ);
        }
    }

    public IMesh build() {
        return builder.build(this);
    }

    public IClientBuffer getBuffer() {
        return buffer;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getOffset() {
        return offset;
    }
}

package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IClientBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.ByteUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.lwjgl.system.MemoryUtil;

public class MeshCollector {

    private final RenderType key;
    private final IClientBuffer buffer;
    private final int offset;
    private final int vertexSize;

    private final long posOffset;
    private final long colorOffset;
    private final long uv0Offset;
    private final long normalOffset;

    private int vertexCount;

    public MeshCollector(
            RenderType key,
            IClientBuffer buffer,
            int offset
    ) {
        this.key = key;
        this.buffer = buffer;
        this.vertexCount = 0;

        this.offset = offset;

        VertexFormat format = key.format;
        this.vertexSize = format.getVertexSize();
        this.colorOffset = format.getOffset(VertexFormatElement.COLOR);
        this.posOffset = format.getOffset(VertexFormatElement.POSITION);
        this.uv0Offset = format.getOffset(VertexFormatElement.UV);
        this.normalOffset = format.getOffset(VertexFormatElement.NORMAL);
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

        if (normalOffset != -1L) {
            ByteUtils.putNormal(vertex + normalOffset + 0L, pNormalX);
            ByteUtils.putNormal(vertex + normalOffset + 1L, pNormalY);
            ByteUtils.putNormal(vertex + normalOffset + 2L, pNormalZ);
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
}

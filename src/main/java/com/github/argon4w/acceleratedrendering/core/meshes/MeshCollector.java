package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IClientBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.ByteBufferUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.system.MemoryUtil;

public class MeshCollector {

    private final RenderType key;
    private final VertexFormat vertexFormat;
    private final IClientBuffer buffer;
    private final int offset;
    private int vertexCount;

    public MeshCollector(
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

        long vertex = this.buffer.reserve(vertexFormat.getVertexSize());
        long posOffset = vertexFormat.getOffset(VertexFormatElement.POSITION);
        long uv0Offset = vertexFormat.getOffset(VertexFormatElement.UV);
        long normalOffset = vertexFormat.getOffset(VertexFormatElement.NORMAL);

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

package com.github.argon4w.acceleratedrendering.builders;

import com.github.argon4w.acceleratedrendering.buffers.IVertexConsumerExtension;

import java.nio.ByteBuffer;

public class OffHeapMesh implements IMesh {

    private final int size;
    private final ByteBuffer vertexBuffer;

    public OffHeapMesh(int size, ByteBuffer vertexBuffer) {
        this.size = size;
        this.vertexBuffer = vertexBuffer;
    }

    @Override
    public void render(IVertexConsumerExtension extension, int color, int light, int overlay) {
        extension.sme$addMesh(vertexBuffer, size, color, light, overlay);
    }
}

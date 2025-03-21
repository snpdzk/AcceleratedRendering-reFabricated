package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.backends.buffers.EmptyServerBuffer;
import com.github.argon4w.acceleratedrendering.core.backends.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.backends.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;

public class ServerMesh implements IMesh {

    private final int size;
    private final int offset;

    public ServerMesh(int size, int offset) {
        this.size = size;
        this.offset = offset;
    }

    @Override
    public void write(
            IAcceleratedVertexConsumer extension,
            int color,
            int light,
            int overlay
    ) {
        extension.addServerMesh(
                offset,
                size,
                color,
                light,
                overlay
        );
    }

    public static class Builder implements IMesh.Builder {

        public static final Builder INSTANCE = new Builder();

        private final Map<VertexFormat, MappedBuffer> storageBuffers;

        private Builder() {
            this.storageBuffers = new Object2ObjectLinkedOpenHashMap<>();
        }

        @Override
        public IMesh build(MeshCollector collector) {
            int vertexCount = collector.getVertexCount();

            if (vertexCount == 0) {
                return EmptyMesh.INSTANCE;
            }

            return new ServerMesh(
                    vertexCount,
                    collector.getOffset()
            );
        }

        @Override
        public MeshCollector newMeshCollector(RenderType renderType) {
            VertexFormat vertexFormat = renderType.format;
            MappedBuffer buffer = storageBuffers.get(vertexFormat);

            if (buffer == null) {
                buffer = new MappedBuffer(1024L, true);
                storageBuffers.put(vertexFormat, buffer);
            }

            return new MeshCollector(
                    this,
                    vertexFormat,
                    buffer,
                    (int) buffer.getPosition()
            );
        }

        public IServerBuffer getStorageBuffer(VertexFormat vertexFormat) {
            IServerBuffer buffer = storageBuffers.get(vertexFormat);

            if (buffer == null) {
                buffer = EmptyServerBuffer.INSTANCE;
            }

            return buffer;
        }
    }
}

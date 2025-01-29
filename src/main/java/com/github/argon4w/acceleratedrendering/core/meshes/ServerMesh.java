package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IVertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.EmptyServerBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;

public class ServerMesh implements IMesh {

    private final RenderType renderType;
    private final int size;
    private final int offset;

    public ServerMesh(RenderType renderType, int size, int offset) {
        this.renderType = renderType;
        this.size = size;
        this.offset = offset;
    }

    @Override
    public void write(IVertexConsumerExtension extension, int color, int light, int overlay) {
        extension.addServerMesh(
                renderType,
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
                    collector.getKey(),
                    vertexCount,
                    collector.getOffset()
            );
        }

        @Override
        public MeshCollector newMeshCollector(RenderType key) {
            VertexFormat vertexFormat = key.format;
            MappedBuffer buffer = storageBuffers.get(vertexFormat);

            if (buffer == null) {
                buffer = new MappedBuffer(1024L);
                storageBuffers.put(vertexFormat, buffer);
            }

            return new MeshCollector(
                    key,
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

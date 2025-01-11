package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IVertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.gl.MappedBuffer;
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
    public void render(IVertexConsumerExtension extension, int color, int light, int overlay) {
        extension.addServerMesh(renderType, offset, size, color, light, overlay);
    }

    public static class Builder implements IMesh.Builder {

        public static final Builder INSTANCE = new Builder();

        private final Map<VertexFormat, MappedBuffer> storageBuffers;

        private Builder() {
            this.storageBuffers = new Object2ObjectLinkedOpenHashMap<>();
        }

        @Override
        public MeshCollector newMeshCollector(RenderType renderType) {
            MappedBuffer buffer = storageBuffers.computeIfAbsent(renderType.format, ignored -> new MappedBuffer(1024L));
            return MeshCollector.create(renderType, buffer, (int) buffer.getPosition());
        }

        @Override
        public IMesh build(MeshCollector meshCollector) {
            return meshCollector.getVertexCount() == 0
                    ? new EmptyMesh()
                    : new ServerMesh(meshCollector.getRenderType(), meshCollector.getVertexCount(), meshCollector.getOffset());
        }

        public MappedBuffer getStorageBuffer(VertexFormat vertexFormat) {
            return storageBuffers.get(vertexFormat);
        }
    }
}

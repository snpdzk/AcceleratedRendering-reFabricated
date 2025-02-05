package com.github.argon4w.acceleratedrendering.core.buffers.fallback;

import com.github.argon4w.acceleratedrendering.core.buffers.pools.ByteBufferBuilderPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.IAcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.mixins.BufferBuilderAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMap;
import net.minecraft.client.renderer.RenderType;

public class VanillaBatchingBufferSource implements IAcceleratedBufferSource {

    private final ByteBufferBuilderPool bufferPool;
    private final Object2ObjectSortedMap<RenderType, BufferBuilder> bufferBuilders;

    public VanillaBatchingBufferSource() {
        this.bufferPool = new ByteBufferBuilderPool();
        this.bufferBuilders = new Object2ObjectLinkedOpenHashMap<>();
    }

    @Override
    public IBufferEnvironment getBufferEnvironment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearBuffers() {
        bufferPool.reset();
        bufferBuilders.clear();
    }

    @Override
    public void drawBuffers() {
        if (bufferBuilders.isEmpty()) {
            return;
        }

        for (RenderType renderType : bufferBuilders.keySet()) {
            BufferBuilder bufferBuilder = bufferBuilders.get(renderType);
            MeshData meshData = bufferBuilder.build();

            if (meshData == null) {
                continue;
            }

            if (renderType.sortOnUpload) {
                meshData.sortQuads(((BufferBuilderAccessor) bufferBuilder).getBuffer(), RenderSystem.getVertexSorting());
            }

            renderType.draw(meshData);
        }
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        BufferBuilder builder = bufferBuilders.get(pRenderType);

        if (builder != null) {
            return builder;
        }

        VertexFormat.Mode mode = pRenderType.mode;
        ByteBufferBuilder buffer = bufferPool.get();

        if (buffer == null) {
            drawBuffers();
            clearBuffers();
            buffer = bufferPool.get();
        }

        builder = new BufferBuilder(
                buffer,
                mode,
                pRenderType.format
        );

        bufferBuilders.put(pRenderType, builder);
        return builder;
    }
}

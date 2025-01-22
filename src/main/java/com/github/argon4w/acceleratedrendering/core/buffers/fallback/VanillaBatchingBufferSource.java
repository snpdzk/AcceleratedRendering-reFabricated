package com.github.argon4w.acceleratedrendering.core.buffers.fallback;

import com.github.argon4w.acceleratedrendering.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.SimpleResetPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.IAcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.mixins.BufferBuilderAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMap;
import net.minecraft.client.renderer.RenderType;

public class VanillaBatchingBufferSource implements IAcceleratedBufferSource {

    private final SimpleResetPool<ByteBufferBuilder> bufferPool;
    private final Object2ObjectSortedMap<RenderType, BufferBuilder> bufferBuilders;

    public VanillaBatchingBufferSource() {
        this.bufferPool = new SimpleResetPool<>(
                CoreFeature.getPooledElementBufferSize(),
                this::newByteBufferBuilder,
                ByteBufferBuilder::clear
        );
        this.bufferBuilders = new Object2ObjectLinkedOpenHashMap<>();
    }

    private ByteBufferBuilder newByteBufferBuilder() {
        return new ByteBufferBuilder(1024);
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
            ByteBufferBuilder buffer = ((BufferBuilderAccessor) bufferBuilder).getBuffer();
            MeshData meshData = bufferBuilders.get(renderType).build();

            if (meshData == null) {
                continue;
            }

            if (renderType.sortOnUpload) {
                meshData.sortQuads(buffer, RenderSystem.getVertexSorting());
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

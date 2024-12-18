package com.example.examplemod;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SequencedMap;

public class BatchedEntityBufferSource implements MultiBufferSource {
    private final SequencedMap<RenderType, BatchedEntityBufferSet> buffers = new Object2ObjectLinkedOpenHashMap<>();
    private final Map<RenderType, SimpleBufferBuilder> startedBuilders;

    public BatchedEntityBufferSource() {
        this.startedBuilders = new Object2ObjectLinkedOpenHashMap<>();
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType pRenderType) {
        SimpleBufferBuilder startedBufferBuilder = startedBuilders.get(pRenderType);

        if (startedBufferBuilder != null) {
            return startedBufferBuilder;
        }

        BatchedEntityBufferSet bufferSet = buffers.get(pRenderType);

        if (bufferSet == null) {
            bufferSet = new BatchedEntityBufferSet(pRenderType);
            buffers.put(pRenderType, bufferSet);
        }

        SimpleBufferBuilder bufferBuilder = new SimpleBufferBuilder(bufferSet.bufferBuilder);

        bufferBuilder.sme$supply(bufferSet, pRenderType);
        startedBuilders.put(pRenderType, bufferBuilder);

        return bufferBuilder;
    }

    public void endAllBatches() {
        for (RenderType renderType : startedBuilders.keySet()) {
            MeshData meshData = startedBuilders.get(renderType).build();

            if (meshData != null) {
                renderType.draw(meshData);
            }
        }

        startedBuilders.clear();
    }

    public void clearBuffers() {
        for (BatchedEntityBufferSet bufferSet : buffers.values()) {
            bufferSet.clear();
        }
    }

    public record BatchedEntityBufferSet(
            ByteBufferBuilder transformIndexBuffer,
            ByteBufferBuilder transformBuffer,
            ByteBufferBuilder normalBuffer,
            ByteBufferBuilder bufferBuilder) implements IEntityBufferSet{
        public BatchedEntityBufferSet(RenderType renderType) {
            this(
                    new ByteBufferBuilder(1024 * 4),
                    new ByteBufferBuilder(1024 * 4 * 4 * 4),
                    new ByteBufferBuilder(1024 * 4 * 4 * 3),
                    new ByteBufferBuilder(renderType.bufferSize)
            );
        }

        public void clear() {
            transformIndexBuffer.clear();
            transformBuffer.clear();
            normalBuffer.clear();
            bufferBuilder.clear();
        }
    }
}

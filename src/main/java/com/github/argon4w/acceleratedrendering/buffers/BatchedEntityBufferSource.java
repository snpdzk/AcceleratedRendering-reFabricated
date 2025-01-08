package com.github.argon4w.acceleratedrendering.buffers;

import com.github.argon4w.acceleratedrendering.builders.SimpleBufferBuilder;
import com.github.argon4w.acceleratedrendering.events.AcceleratedRenderingClientModEvents;
import com.github.argon4w.acceleratedrendering.gl.GLSingleFenceSync;
import com.github.argon4w.acceleratedrendering.gl.ResizableMappedGLBuffer;
import com.github.argon4w.acceleratedrendering.utils.ByteBufferUtils;
import com.github.argon4w.acceleratedrendering.utils.GLUtils;
import com.github.argon4w.acceleratedrendering.utils.IndexUtils;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SequencedMap;

import static org.lwjgl.opengl.GL46.*;

public class BatchedEntityBufferSource implements MultiBufferSource {

    public static final BatchedEntityBufferSource INSTANCE = new BatchedEntityBufferSource();
    public static boolean acceleratedRendering;

    private final SequencedMap<RenderType, IEntityBuffers> buffers;
    private final Map<RenderType, VertexConsumer> startedBuilders;

    private final ResizableMappedGLBuffer poseBuffer;
    private final ResizableMappedGLBuffer varyingBuffer;
    private final ResizableMappedGLBuffer vertexBuffer;
    private final GLSingleFenceSync fenceSync;

    private int pose;
    private int index;

    public BatchedEntityBufferSource() {
        this.buffers = new Object2ObjectLinkedOpenHashMap<>();
        this.startedBuilders = new Object2ObjectLinkedOpenHashMap<>();

        this.poseBuffer = new ResizableMappedGLBuffer((4L * 4L * 4L + 4L * 4L * 3L) * 1024L);
        this.varyingBuffer = new ResizableMappedGLBuffer(4L * 4L * 1024L);
        this.vertexBuffer = new ResizableMappedGLBuffer(DefaultVertexFormat.NEW_ENTITY.getVertexSize() * 1024L);
        this.fenceSync = new GLSingleFenceSync();

        this.pose = -1;
        this.index = -1;
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType pRenderType) {
        VertexConsumer startedBufferBuilder = startedBuilders.get(pRenderType);

        if (startedBufferBuilder != null) {
            return startedBufferBuilder;
        }

        boolean accelerated = pRenderType.format == DefaultVertexFormat.NEW_ENTITY;
        IEntityBuffers bufferSet = buffers.get(pRenderType);

        if (bufferSet == null) {
            bufferSet = accelerated
                    ? new BatchedEntityBuffers(this, pRenderType)
                    : new FallbackEntityBuffers(pRenderType);

            buffers.put(pRenderType, bufferSet);
        }

        VertexConsumer bufferBuilder = bufferSet.newBufferBuilder();
        startedBuilders.put(pRenderType, bufferBuilder);

        return bufferBuilder;
    }

    public void drawBuffers() {
        if (startedBuilders.isEmpty()) {
            return;
        }

        int program = GLUtils.getCurrentProgram();
        GLUtils.useProgram(AcceleratedRenderingClientModEvents.getShaderProgram());

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, vertexBuffer.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, poseBuffer.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, varyingBuffer.getBufferHandle());

        GLUtils.dispatchCompute((int) vertexBuffer.getPosition() / DefaultVertexFormat.NEW_ENTITY.getVertexSize());
        GLUtils.waitForShaderStorage();

        GLUtils.useProgram(program);

        acceleratedRendering = true;
        vertexBuffer.unmap();

        for (RenderType renderType : startedBuilders.keySet()) {
            MeshData meshData = ((IVertexConsumerExtension) startedBuilders.get(renderType)).sme$build();

            if (meshData != null) {
                renderType.draw(meshData);
            }
        }

        startedBuilders.clear();
        fenceSync.fenceSync();
        acceleratedRendering = false;
    }

    public void clearBuffers() {
        for (IEntityBuffers buffers : buffers.values()) {
            buffers.clear();
        }

        poseBuffer.reset();
        varyingBuffer.reset();
        vertexBuffer.reset();

        pose = -1;
        index = -1;
    }

    public void waitFenceSync() {
        fenceSync.clientWaitSync();
    }

    public ResizableMappedGLBuffer getPoseBuffer() {
        return poseBuffer;
    }

    public ResizableMappedGLBuffer getVaryingBuffer() {
        return varyingBuffer;
    }

    public ResizableMappedGLBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public int getPose() {
        return ++ this.pose;
    }

    public int getIndex() {
        return ++ this.index;
    }

    public static boolean isAcceleratedRendering() {
        return acceleratedRendering;
    }

    public record BatchedEntityBuffers(
            ByteBufferBuilder indexBuffer,
            BatchedEntityBufferSource bufferSource,
            RenderType renderType) implements IEntityBuffers {

        public BatchedEntityBuffers(
                BatchedEntityBufferSource bufferSource,
                RenderType renderType) {
            this(
                    new ByteBufferBuilder(4 * 1024),
                    bufferSource,
                    renderType);
        }

        @Override
        public void clear() {
            indexBuffer.clear();
        }

        @Override
        public VertexConsumer newBufferBuilder() {
            return SimpleBufferBuilder.create(this, renderType);
        }

        @Override
        public ByteBufferBuilder.Result buildIndexBuffer(int count) {
            return IndexUtils.buildIndexBuffer(count, indexBuffer.build(), indexBuffer);
        }

        @Override
        public long reservePose() {
            return bufferSource.getPoseBuffer().reserve(4L * 4L * 4L + 4L * 4L * 3L);
        }

        @Override
        public long reserveVertex() {
            return bufferSource.getVertexBuffer().reserve(DefaultVertexFormat.NEW_ENTITY.getVertexSize());
        }

        @Override
        public long reserveVertices(long count) {
            return bufferSource.getVertexBuffer().reserve(DefaultVertexFormat.NEW_ENTITY.getVertexSize() * count);
        }

        @Override
        public long reserveVarying() {
            return bufferSource.getVaryingBuffer().reserve(4L * 4L);
        }

        @Override
        public long reserveVaryings(long count) {
            return  bufferSource.getVaryingBuffer().reserve(4L * 4L * count);
        }

        @Override
        public void reserveIndex() {
            ByteBufferUtils.putInt(indexBuffer.reserve(4), bufferSource.getIndex());
        }

        @Override
        public int getPose() {
            return bufferSource.getPose();
        }
    }

    public record FallbackEntityBuffers(
            RenderType renderType,
            ByteBufferBuilder vertexBuffer) implements IEntityBuffers {

        public FallbackEntityBuffers(RenderType renderType) {
            this(
                    renderType,
                    new ByteBufferBuilder(renderType.bufferSize));
        }

        @Override
        public VertexConsumer newBufferBuilder() {
            return new BufferBuilder(
                    vertexBuffer,
                    renderType.mode,
                    renderType.format
            );
        }

        @Override
        public ByteBufferBuilder.Result buildIndexBuffer(int count) {
            return null;
        }

        @Override
        public long reservePose() {
            return -1;
        }

        @Override
        public long reserveVertices(long count) {
            return -1;
        }

        @Override
        public long reserveVertex() {
            return -1;
        }

        @Override
        public long reserveVarying() {
            return 0;
        }

        @Override
        public long reserveVaryings(long count) {
            return 0;
        }

        @Override
        public void reserveIndex() {

        }

        @Override
        public int getPose() {
            return -1;
        }

        @Override
        public void clear() {
            vertexBuffer.clear();
        }
    }
}

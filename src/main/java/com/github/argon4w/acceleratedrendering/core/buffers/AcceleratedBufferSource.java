package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.meshes.ServerMesh;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class AcceleratedBufferSource extends MultiBufferSource.BufferSource implements IAcceleratedBufferSource {

    public static final AcceleratedBufferSource CORE = new AcceleratedBufferSource(DefaultVertexFormat.NEW_ENTITY, ComputeShaderPrograms.CORE_ENTITY_COMPUTE_SHADER_KEY);

    private final VertexFormat vertexFormat;
    private final ResourceLocation programKey;

    private final Map<RenderType, IAcceleratedBuffers> acceleratedBuffers;
    private final Map<RenderType, ByteBufferBuilder> vanillaBuffers;

    private final Map<RenderType, AcceleratedBufferBuilder> acceleratedBuilders;
    private final Map<RenderType, BufferBuilder> vanillaBuilders;

    private final MappedBuffer poseBuffer;
    private final MappedBuffer varyingBuffer;
    private final MappedBuffer vertexBuffer;

    private final int vaoHandle;

    private int pose;
    private int index;

    public AcceleratedBufferSource(VertexFormat vertexFormat, ResourceLocation programKey) {
        super(null, null);

        this.vertexFormat = vertexFormat;
        this.programKey = programKey;

        this.acceleratedBuffers = new Object2ObjectLinkedOpenHashMap<>();
        this.vanillaBuffers = new Object2ObjectLinkedOpenHashMap<>();

        this.acceleratedBuilders = new Object2ObjectLinkedOpenHashMap<>();
        this.vanillaBuilders = new Object2ObjectLinkedOpenHashMap<>();

        this.poseBuffer = new MappedBuffer((4L * 4L * 4L + 4L * 4L * 3L) * 1024L);
        this.varyingBuffer = new MappedBuffer(5L * 4L * 1024L);
        this.vertexBuffer = new MappedBuffer(this.vertexFormat.getVertexSize() * 1024L);

        this.vaoHandle = glCreateVertexArrays();

        this.pose = -1;
        this.index = 0;
    }

    @Override
    public void endLastBatch() {

    }

    @Override
    public void endBatch() {

    }

    @Override
    public void endBatch(RenderType pRenderType) {

    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType pRenderType) {
        return pRenderType.format == vertexFormat
                ? getAcceleratedBuffer(pRenderType)
                : getVanillaBuffer(pRenderType);
    }

    @Override
    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    @Override
    public MappedBuffer getPoseBuffer() {
        return poseBuffer;
    }

    @Override
    public MappedBuffer getVaryingBuffer() {
        return varyingBuffer;
    }

    @Override
    public MappedBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    @Override
    public int getPose() {
        return ++ this.pose;
    }

    @Override
    public int getIndex(int count) {
        int index = this.index;
        this.index += count;

        return index;
    }

    public void drawBuffers() {
        if (!vanillaBuilders.isEmpty()) {
            drawVanillaBuffers();
        }

        if (!acceleratedBuilders.isEmpty()) {
            drawAcceleratedBuffers();
        }
    }

    public VertexConsumer getAcceleratedBuffer(RenderType renderType) {
        AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);

        if (builder != null) {
            return builder;
        }

        IAcceleratedBuffers buffers = acceleratedBuffers.get(renderType);

        if (buffers == null) {
            buffers = new AcceleratedBuffers(this, renderType.mode);
            acceleratedBuffers.put(renderType, buffers);
        }

        builder = AcceleratedBufferBuilder.create(buffers, renderType);
        acceleratedBuilders.put(renderType, builder);

        return builder;
    }

    public VertexConsumer getVanillaBuffer(RenderType renderType) {
        BufferBuilder builder = vanillaBuilders.get(renderType);

        if (builder != null) {
            return builder;
        }

        ByteBufferBuilder buffer = vanillaBuffers.get(renderType);

        if (buffer == null) {
            buffer = new ByteBufferBuilder(renderType.bufferSize);
            vanillaBuffers.put(renderType, buffer);
        }

        builder = new BufferBuilder(buffer, renderType.mode, renderType.format);
        vanillaBuilders.put(renderType, builder);

        return builder;
    }

    public void drawAcceleratedBuffers() {
        int program = glGetInteger(GL_CURRENT_PROGRAM);
        ComputeShaderPrograms.useProgram(programKey);

        MappedBuffer meshStorageBuffer = ServerMesh.Builder.INSTANCE.getStorageBuffer(vertexFormat);

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, vertexBuffer.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, poseBuffer.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, varyingBuffer.getBufferHandle());

        if (meshStorageBuffer != null) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, meshStorageBuffer.getBufferHandle());
        }

        glDispatchCompute((int) vertexBuffer.getPosition() / vertexFormat.getVertexSize(), 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);

        glUseProgram(program);

        vertexBuffer.unmap();
        BufferUploader.reset();
        glBindVertexArray(vaoHandle);

        for (RenderType renderType : acceleratedBuilders.keySet()) {
            VertexFormat.Mode mode = renderType.mode;
            IAcceleratedBuffers buffers = acceleratedBuffers.get(renderType);
            AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);
            MappedBuffer indexBuffer = buffers.getIndexBuffer();
            int vertexCount = builder.getVertexCount();

            indexBuffer.unmap();
            glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer.getBufferHandle());
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferHandle());

            renderType.setupRenderState();
            vertexFormat.setupBufferState();

            int indexCount = mode.indexCount(vertexCount);
            ShaderInstance shader = RenderSystem.getShader();

            shader.setDefaultUniforms(
                    mode,
                    RenderSystem.getModelViewMatrix(),
                    RenderSystem.getProjectionMatrix(),
                    Minecraft.getInstance().getWindow());
            shader.apply();

            glDrawElements(
                    mode.asGLMode,
                    indexCount,
                    VertexFormat.IndexType.INT.asGLType,
                    0);

            shader.clear();
            renderType.clearRenderState();
        }

        glBindVertexArray(0);
        acceleratedBuilders.clear();
    }

    public void drawVanillaBuffers() {
        for (RenderType renderType : vanillaBuilders.keySet()) {
            MeshData meshData = vanillaBuilders.get(renderType).build();

            if (meshData!= null) {
                renderType.draw(meshData);
            }
        }

        vanillaBuilders.clear();
    }

    public void clearBuffers() {
        for (IAcceleratedBuffers buffers : acceleratedBuffers.values()) {
            buffers.clear();
        }

        for (ByteBufferBuilder builder : vanillaBuffers.values()) {
            builder.clear();
        }

        poseBuffer.reset();
        varyingBuffer.reset();
        vertexBuffer.reset();

        pose = -1;
        index = 0;
    }

    public void mapBuffers() {
        vertexBuffer.map();

        for (IAcceleratedBuffers buffers : acceleratedBuffers.values()) {
            buffers.getIndexBuffer().map();
        }
    }
}

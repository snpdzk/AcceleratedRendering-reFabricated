package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.CommandBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MutableBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
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

    public static final AcceleratedBufferSource CORE = new AcceleratedBufferSource(
            DefaultVertexFormat.NEW_ENTITY,
            ComputeShaderPrograms.CORE_ENTITY_COMPUTE_SHADER_KEY,
            ComputeShaderPrograms.CORE_ENTITY_POLYGON_CULL_KEY
    );

    private final VertexFormat vertexFormat;

    private final Program transformProgram;
    private final Program cullProgram;
    private final Uniform uniform;

    private final Map<RenderType, IAcceleratedBuffers> acceleratedBuffers;
    private final Map<RenderType, ByteBufferBuilder> vanillaBuffers;

    private final Map<RenderType, AcceleratedBufferBuilder> acceleratedBuilders;
    private final Map<RenderType, BufferBuilder> vanillaBuilders;

    private final CommandBuffer commandBuffer;
    private final MappedBuffer poseBuffer;
    private final MappedBuffer varyingBuffer;
    private final MappedBuffer vertexBufferIn;
    private final MutableBuffer vertexBufferOut;
    private final MutableBuffer indexBufferOut;

    private final int vaoHandle;

    private int pose;
    private int index;

    public AcceleratedBufferSource(
            VertexFormat vertexFormat,
            ResourceLocation transformProgramKey,
            ResourceLocation cullProgramKey
    ) {
        super(null, null);

        this.vertexFormat = vertexFormat;

        this.transformProgram = ComputeShaderPrograms.getProgram(transformProgramKey);
        this.cullProgram = ComputeShaderPrograms.getProgram(cullProgramKey);
        this.uniform = this.cullProgram.getUniform("ViewMatrix");

        this.acceleratedBuffers = new Object2ObjectLinkedOpenHashMap<>();
        this.vanillaBuffers = new Object2ObjectLinkedOpenHashMap<>();

        this.acceleratedBuilders = new Object2ObjectLinkedOpenHashMap<>();
        this.vanillaBuilders = new Object2ObjectLinkedOpenHashMap<>();

        this.commandBuffer = new CommandBuffer();
        this.poseBuffer = new MappedBuffer((4L * 4L * 4L + 4L * 4L * 3L) * 1024L);
        this.varyingBuffer = new MappedBuffer(5L * 4L * 1024L);
        this.vertexBufferIn = new MappedBuffer(this.vertexFormat.getVertexSize() * 1024L);
        this.vertexBufferOut = new MutableBuffer(this.vertexFormat.getVertexSize() * 1024L, GL_DYNAMIC_STORAGE_BIT);
        this.indexBufferOut = new MutableBuffer(4L * 1024L, GL_DYNAMIC_STORAGE_BIT);

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
        return vertexBufferIn;
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
        vertexBufferOut.resizeTo(vertexBufferIn.getBufferSize());
        transformProgram.useProgram();

        MappedBuffer meshStorageBuffer = ServerMesh.Builder.INSTANCE.getStorageBuffer(vertexFormat);

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, vertexBufferIn.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, vertexBufferOut.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, poseBuffer.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, varyingBuffer.getBufferHandle());

        if (meshStorageBuffer != null) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 4, meshStorageBuffer.getBufferHandle());
        }

        glDispatchCompute((int) vertexBufferIn.getPosition() / vertexFormat.getVertexSize(), 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);

        transformProgram.resetProgram();

        BufferUploader.reset();
        glBindVertexArray(vaoHandle);
        uniform.upload(RenderSystem.getModelViewMatrix());

        for (RenderType renderType : acceleratedBuilders.keySet()) {
            VertexFormat.Mode mode = renderType.mode;
            IAcceleratedBuffers buffers = acceleratedBuffers.get(renderType);
            AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);
            MappedBuffer indexBuffer = buffers.getIndexBuffer();

            indexBufferOut.resizeTo(indexBuffer.getBufferSize());
            cullProgram.useProgram();

            glBindBufferBase(GL_ATOMIC_COUNTER_BUFFER, 0, commandBuffer.getBufferHandle());
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 5, indexBuffer.getBufferHandle());
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 6, indexBufferOut.getBufferHandle());

            glDispatchCompute(mode.indexCount(builder.getVertexCount()) / 3, 1, 1);
            glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT | GL_ATOMIC_COUNTER_BARRIER_BIT);

            cullProgram.resetProgram();

            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferOut.getBufferHandle());
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferOut.getBufferHandle());
            glBindBuffer(GL_DRAW_INDIRECT_BUFFER, commandBuffer.getBufferHandle());

            renderType.setupRenderState();
            vertexFormat.setupBufferState();

            ShaderInstance shader = RenderSystem.getShader();

            shader.setDefaultUniforms(
                    mode,
                    RenderSystem.getModelViewMatrix(),
                    RenderSystem.getProjectionMatrix(),
                    Minecraft.getInstance().getWindow());
            shader.apply();

            glDrawElementsIndirect(
                    mode.asGLMode,
                    VertexFormat.IndexType.INT.asGLType,
                    0
            );
            glMemoryBarrier(GL_COMMAND_BARRIER_BIT);

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
        vertexBufferIn.reset();

        pose = -1;
        index = 0;
    }
}

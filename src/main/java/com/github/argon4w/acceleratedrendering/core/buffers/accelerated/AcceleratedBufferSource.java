package com.github.argon4w.acceleratedrendering.core.buffers.accelerated;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgram;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class AcceleratedBufferSource extends MultiBufferSource.BufferSource implements IAcceleratedBufferSource {

    private final IBufferEnvironment bufferEnvironment;
    private final AcceleratedBufferSetPool acceleratedBufferSetPool;
    private final Map<RenderType, AcceleratedBufferBuilder> acceleratedBuilders;

    private AcceleratedBufferSetPool.BufferSet bufferSet;

    public AcceleratedBufferSource(IBufferEnvironment bufferEnvironment) {
        super(null, null);

        this.bufferEnvironment = bufferEnvironment;
        this.acceleratedBufferSetPool = new AcceleratedBufferSetPool(this.bufferEnvironment);
        this.acceleratedBuilders = new Object2ObjectLinkedOpenHashMap<>();

        this.bufferSet = acceleratedBufferSetPool.getBufferSet();
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
    public IBufferEnvironment getBufferEnvironment() {
        return bufferEnvironment;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);

        if (builder != null) {
            return builder;
        }

        ElementBuffer elementBuffer = bufferSet.getElementBuffer();

        if (elementBuffer == null) {
            drawBuffers();
            clearBuffers();
            elementBuffer = bufferSet.getElementBuffer();
        }

        builder = AcceleratedBufferBuilder.create(
                elementBuffer.setMode(renderType.mode),
                bufferEnvironment,
                bufferSet,
                renderType
        );

        acceleratedBuilders.put(renderType, builder);
        return builder;
    }

    @Override
    public void drawBuffers() {
        if (acceleratedBuilders.isEmpty()) {
            return;
        }

        Program transformProgram = bufferEnvironment.selectTransformProgram();
        transformProgram.useProgram();

        bufferSet.bindTransformBuffers();
        bufferEnvironment.getServerMeshBuffer().bindBase(GL_SHADER_STORAGE_BUFFER, 4);

        glDispatchCompute(bufferSet.getVertexCount(), 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);

        transformProgram.resetProgram();

        BufferUploader.reset();
        bufferSet.bindVertexArray();

        for (RenderType renderType : acceleratedBuilders.keySet()) {
            AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);
            ElementBuffer elementBuffer = builder.getElementBuffer();

            if (elementBuffer.getPosition() == 0) {
                continue;
            }

            VertexFormat.Mode mode = renderType.mode;
            ICullingProgram program = bufferEnvironment.selectCullProgram(renderType);

            program.useProgram();
            program.uploadUniforms();

            bufferSet.bindCullingBuffers(elementBuffer.getBufferSize());
            elementBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 5);

            int count = program.getCount(
                    mode,
                    elementBuffer,
                    builder
            );

            glDispatchCompute(count, 1, 1);

            glMemoryBarrier(
                    GL_SHADER_STORAGE_BARRIER_BIT
                            | GL_ATOMIC_COUNTER_BARRIER_BIT
            );

            program.resetProgram();

            bufferSet.bindDrawBuffers();

            renderType.setupRenderState();
            bufferEnvironment.setupBufferState();

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

            glMemoryBarrier(
                    GL_ELEMENT_ARRAY_BARRIER_BIT
                            | GL_COMMAND_BARRIER_BIT
            );

            shader.clear();
            renderType.clearRenderState();
        }

        bufferSet.resetVertexArray();
        acceleratedBuilders.clear();
    }

    @Override
    public void clearBuffers() {
        bufferSet.reset();
        bufferSet.setInFlight();
        bufferSet = acceleratedBufferSetPool.getBufferSet();
    }
}

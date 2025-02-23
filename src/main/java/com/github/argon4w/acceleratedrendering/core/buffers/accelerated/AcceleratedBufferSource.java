package com.github.argon4w.acceleratedrendering.core.buffers.accelerated;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.DrawContextPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.ElementBufferPool;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
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
    private final Map<RenderType, DrawContextPool.IndirectDrawContext> drawContexts;

    private AcceleratedBufferSetPool.BufferSet bufferSet;


    public AcceleratedBufferSource(IBufferEnvironment bufferEnvironment) {
        super(null, null);

        this.bufferEnvironment = bufferEnvironment;
        this.acceleratedBufferSetPool = new AcceleratedBufferSetPool(this.bufferEnvironment);
        this.acceleratedBuilders = new Object2ObjectLinkedOpenHashMap<>();
        this.drawContexts = new Object2ObjectLinkedOpenHashMap<>();

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

        ElementBufferPool.ElementBuffer elementBuffer = bufferSet.getElementBuffer();

        if (elementBuffer == null) {
            drawBuffers();
            clearBuffers();
            elementBuffer = bufferSet.getElementBuffer();
        }

        builder = new AcceleratedBufferBuilder(
                elementBuffer,
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

        bufferSet.bindTransformBuffers();
        bufferSet.prepareElementBuffer();
        bufferEnvironment.selectTransformProgramDispatcher().dispatch(bufferSet.getVertexCount());

        int program = glGetInteger(GL_CURRENT_PROGRAM);
        int barrier = 0;

        for (RenderType renderType : acceleratedBuilders.keySet()) {
            AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);
            ElementBufferPool.ElementBuffer elementBuffer = builder.getElementBuffer();

            if (elementBuffer.getPosition() == 0) {
                continue;
            }

            VertexFormat.Mode mode = renderType.mode;
            int vertexCount = builder.getVertexCount();
            DrawContextPool.IndirectDrawContext drawContext = bufferSet.getDrawContext();

            drawContext.bindComputeBuffers(elementBuffer);
            drawContexts.put(renderType, drawContext);

            barrier |= bufferEnvironment.selectProcessingProgramDispatcher(mode).dispatch(mode, vertexCount);
            barrier |= bufferEnvironment.selectCullProgramDispatcher(renderType).dispatch(mode, vertexCount);
        }

        glMemoryBarrier(barrier);
        glUseProgram(program);

        BufferUploader.invalidate();
        bufferSet.bindVertexArray();
        bufferSet.bindDrawBuffers();

        for (RenderType renderType : drawContexts.keySet()) {
            renderType.setupRenderState();

            DrawContextPool.IndirectDrawContext drawContext = drawContexts.get(renderType);
            VertexFormat.Mode mode = renderType.mode;
            ShaderInstance shader = RenderSystem.getShader();

            shader.setDefaultUniforms(
                    mode,
                    RenderSystem.getModelViewMatrix(),
                    RenderSystem.getProjectionMatrix(),
                    Minecraft.getInstance().getWindow()
            );
            shader.apply();

            drawContext.drawElements(mode);
            shader.clear();
            renderType.clearRenderState();
        }

        DrawContextPool.waitBarriers();
        bufferSet.resetVertexArray();
        acceleratedBuilders.clear();
    }

    @Override
    public void clearBuffers() {
        drawContexts.clear();
        bufferSet.reset();
        bufferSet.setInFlight();
        bufferSet = acceleratedBufferSetPool.getBufferSet();
    }
}

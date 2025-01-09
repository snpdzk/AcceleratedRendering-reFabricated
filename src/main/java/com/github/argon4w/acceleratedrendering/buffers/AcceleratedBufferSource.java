package com.github.argon4w.acceleratedrendering.buffers;

import com.github.argon4w.acceleratedrendering.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.events.AcceleratedRenderingClientModEvents;
import com.github.argon4w.acceleratedrendering.gl.GLSingleFenceSync;
import com.github.argon4w.acceleratedrendering.gl.ResizableMappedGLBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class AcceleratedBufferSource implements MultiBufferSource {

    public static final AcceleratedBufferSource INSTANCE = new AcceleratedBufferSource();

    private final Map<RenderType, AcceleratedBuffers> acceleratedBuffers;
    private final Map<RenderType, ByteBufferBuilder> vanillaBuffers;

    private final Map<RenderType, AcceleratedBufferBuilder> acceleratedBuilders;
    private final Map<RenderType, BufferBuilder> vanillaBuilders;

    private final ResizableMappedGLBuffer poseBuffer;
    private final ResizableMappedGLBuffer varyingBuffer;
    private final ResizableMappedGLBuffer vertexBuffer;
    private final GLSingleFenceSync fenceSync;

    private final int vaoHandle;
    private final int eboHandle;

    private int pose;
    private int index;

    public AcceleratedBufferSource() {
        this.acceleratedBuffers = new Object2ObjectLinkedOpenHashMap<>();
        this.vanillaBuffers = new Object2ObjectLinkedOpenHashMap<>();

        this.acceleratedBuilders = new Object2ObjectLinkedOpenHashMap<>();
        this.vanillaBuilders = new Object2ObjectLinkedOpenHashMap<>();

        this.poseBuffer = new ResizableMappedGLBuffer((4L * 4L * 4L + 4L * 4L * 3L) * 1024L);
        this.varyingBuffer = new ResizableMappedGLBuffer(4L * 4L * 1024L);
        this.vertexBuffer = new ResizableMappedGLBuffer(DefaultVertexFormat.NEW_ENTITY.getVertexSize() * 1024L);
        this.fenceSync = new GLSingleFenceSync();

        this.vaoHandle = glCreateVertexArrays();
        this.eboHandle = glCreateBuffers();

        this.pose = -1;
        this.index = -1;
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType pRenderType) {
        return pRenderType.format == DefaultVertexFormat.NEW_ENTITY
                ? getAcceleratedBuffer(pRenderType)
                : getVanillaBuffer(pRenderType);
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

        AcceleratedBuffers buffers = acceleratedBuffers.get(renderType);

        if (buffers == null) {
            buffers = new AcceleratedBuffers(this);
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
        glUseProgram(AcceleratedRenderingClientModEvents.getShaderProgram());

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, vertexBuffer.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, poseBuffer.getBufferHandle());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, varyingBuffer.getBufferHandle());

        glDispatchCompute((int) vertexBuffer.getPosition() / DefaultVertexFormat.NEW_ENTITY.getVertexSize(), 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);

        glUseProgram(program);
        BufferUploader.reset();

        vertexBuffer.unmap();
        glBindVertexArray(vaoHandle);

        for (RenderType renderType : acceleratedBuilders.keySet()) {
            AcceleratedBuffers buffers = acceleratedBuffers.get(renderType);
            AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);
            int vertices = builder.getVertices();

            try (ByteBufferBuilder.Result indexBuffer = buffers.buildIndexBuffer(vertices)) {
                if (indexBuffer == null) {
                    continue;
                }

                glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer.getBufferHandle());
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboHandle);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.byteBuffer(), GL_DYNAMIC_DRAW);

                renderType.setupRenderState();
                DefaultVertexFormat.NEW_ENTITY.setupBufferState();

                int indices = VertexFormat.Mode.QUADS.indexCount(vertices);
                ShaderInstance shader = RenderSystem.getShader();

                shader.setDefaultUniforms(
                        VertexFormat.Mode.QUADS,
                        RenderSystem.getModelViewMatrix(),
                        RenderSystem.getProjectionMatrix(),
                        Minecraft.getInstance().getWindow());
                shader.apply();

                glDrawElements(
                        VertexFormat.Mode.QUADS.asGLMode,
                        indices,
                        VertexFormat.IndexType.INT.asGLType,
                        0L);

                shader.clear();
                renderType.clearRenderState();
            }
        }

        glBindVertexArray(0);
        acceleratedBuilders.clear();
        fenceSync.fenceSync();
    }

    public void drawVanillaBuffers() {
        for (RenderType renderType : vanillaBuilders.keySet()) {
            MeshData meshData = vanillaBuilders.get(renderType).build();

            if (meshData!= null) {
                //renderType.draw(meshData);
                meshData.close();
            }
        }

        vanillaBuilders.clear();
    }

    public void clearBuffers() {
        for (AcceleratedBuffers buffers : acceleratedBuffers.values()) {
            buffers.clear();
        }

        for (ByteBufferBuilder builder : vanillaBuffers.values()) {
            builder.clear();
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
}

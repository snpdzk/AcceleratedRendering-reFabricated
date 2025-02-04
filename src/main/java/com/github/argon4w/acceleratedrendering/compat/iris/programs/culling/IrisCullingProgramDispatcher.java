package com.github.argon4w.acceleratedrendering.compat.iris.programs.culling;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class IrisCullingProgramDispatcher implements IProgramDispatcher {

    private final ComputeProgram program;
    private final Uniform uniform;

    private IrisCullingProgramDispatcher(ComputeProgram program) {
        this.program = program;
        this.uniform = program.getUniform("ViewMatrix");
    }

    public IrisCullingProgramDispatcher(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public void dispatch(
            VertexFormat.Mode mode,
            ElementBuffer elementBuffer,
            AcceleratedBufferBuilder builder
    ) {
        int count = mode.indexCount(builder.getVertexCount()) / 3;

        uniform.upload(getModelViewMatrix());
        program.dispatch(count, 1, 1);
    }

    private Matrix4f getModelViewMatrix() {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered()
                ? ShadowRenderer.MODELVIEW
                : RenderSystem.getModelViewMatrix();
    }
}

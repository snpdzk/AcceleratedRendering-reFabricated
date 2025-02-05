package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class NormalCullingProgramDispatcher implements IProgramDispatcher {

    private final ComputeProgram program;
    private final Uniform uniform;

    private NormalCullingProgramDispatcher(ComputeProgram program) {
        this.program = program;
        this.uniform = program.getUniform("ViewMatrix");
    }

    public NormalCullingProgramDispatcher(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public void dispatch(VertexFormat.Mode mode, int vertexCount) {
        uniform.upload(RenderSystem.getModelViewMatrix());
        program.dispatch(mode.indexCount(vertexCount) / 3);
    }
}

package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgram;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class NormalCullingProgram implements ICullingProgram {

    private final Program program;
    private final Uniform uniform;

    private NormalCullingProgram(Program program) {
        this.program = program;
        this.uniform = program.getUniform("ViewMatrix");
    }

    public NormalCullingProgram(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public int getCount(
            VertexFormat.Mode mode,
            ElementBuffer elementBuffer,
            AcceleratedBufferBuilder builder
    ) {
        return mode.indexCount(builder.getVertexCount()) / 3;
    }

    @Override
    public void uploadUniforms() {
        uniform.upload(RenderSystem.getModelViewMatrix());
    }

    @Override
    public void useProgram() {
        program.useProgram();
    }

    @Override
    public void resetProgram() {
        program.resetProgram();
    }
}

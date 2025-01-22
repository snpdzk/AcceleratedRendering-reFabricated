package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.mojang.blaze3d.vertex.VertexFormat;

public class PassThroughCullingProgram implements ICullingProgram {

    public static final PassThroughCullingProgram INSTANCE = new PassThroughCullingProgram();

    private final Program program;

    public PassThroughCullingProgram() {
        this.program = ComputeShaderProgramLoader.getProgram(ComputeShaderPrograms.CORE_PASS_THROUGH_POLYGON_CULLING_KEY);
    }

    @Override
    public int getCount(
            VertexFormat.Mode mode,
            ElementBuffer elementBuffer,
            AcceleratedBufferBuilder builder
    ) {
        return mode.indexCount(builder.getVertexCount());
    }

    @Override
    public void uploadUniforms() {

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

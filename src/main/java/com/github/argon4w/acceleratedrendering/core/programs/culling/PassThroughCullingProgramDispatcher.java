package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;

import static org.lwjgl.opengl.GL46.*;

public class PassThroughCullingProgramDispatcher implements IProgramDispatcher {

    public static final PassThroughCullingProgramDispatcher INSTANCE = new PassThroughCullingProgramDispatcher();

    private final ComputeProgram program;

    public PassThroughCullingProgramDispatcher() {
        this.program = ComputeShaderProgramLoader.getProgram(ComputeShaderPrograms.CORE_PASS_THROUGH_POLYGON_CULLING_KEY);
    }

    @Override
    public void dispatch(
            VertexFormat.Mode mode,
            ElementBuffer elementBuffer,
            AcceleratedBufferBuilder builder
    ) {

        int count = mode.indexCount(builder.getVertexCount());
        program.dispatch(count, 1, 1);
    }
}

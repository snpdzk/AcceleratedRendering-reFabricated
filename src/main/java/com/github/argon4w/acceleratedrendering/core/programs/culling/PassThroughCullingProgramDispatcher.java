package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;

public class PassThroughCullingProgramDispatcher implements IProgramDispatcher {

    public static final PassThroughCullingProgramDispatcher INSTANCE = new PassThroughCullingProgramDispatcher();

    private final ComputeProgram program;

    public PassThroughCullingProgramDispatcher() {
        this.program = ComputeShaderProgramLoader.getProgram(ComputeShaderPrograms.CORE_PASS_THROUGH_POLYGON_CULLING_KEY);
    }

    @Override
    public void dispatch(VertexFormat.Mode mode, int vertexCount) {
        program.dispatch(mode.indexCount(vertexCount));
    }
}

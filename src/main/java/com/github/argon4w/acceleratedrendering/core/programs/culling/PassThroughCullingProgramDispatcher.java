package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;

public class PassThroughCullingProgramDispatcher implements IPolygonProgramDispatcher {

    public static final PassThroughCullingProgramDispatcher INSTANCE = new PassThroughCullingProgramDispatcher();

    private static final int GROUP_SIZE = 128;

    private final ComputeProgram program;
    private final Uniform indexCountUniform;

    public PassThroughCullingProgramDispatcher() {
        this.program = ComputeShaderProgramLoader.getProgram(ComputeShaderPrograms.CORE_PASS_THROUGH_POLYGON_CULLING_KEY);
        this.indexCountUniform = program.getUniform("indexCount");
    }

    @Override
    public void dispatch(VertexFormat.Mode mode, int vertexCount) {
        int indexCount = mode.indexCount(vertexCount);

        indexCountUniform.uploadUnsignedInt(indexCount);
        program.dispatch((indexCount + GROUP_SIZE - 1) / GROUP_SIZE);
    }
}

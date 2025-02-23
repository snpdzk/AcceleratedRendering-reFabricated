package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class IrisProcessingProgramDispatcher implements IPolygonProgramDispatcher {

    private static final int GROUP_SIZE = 128;

    private final ComputeProgram program;
    private final Uniform polygonCountUniform;

    public IrisProcessingProgramDispatcher(ComputeProgram program) {
        this.program = program;
        this.polygonCountUniform = this.program.getUniform("polygonCount");
    }

    public IrisProcessingProgramDispatcher(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public int dispatch(VertexFormat.Mode mode, int vertexCount) {
        int polygonCount = vertexCount / mode.primitiveLength;

        polygonCountUniform.uploadUnsignedInt(polygonCount);
        program.useProgram();
        program.dispatch((polygonCount + GROUP_SIZE - 1) / GROUP_SIZE);
        program.resetProgram();

        return program.getBarrierFlags();
    }
}

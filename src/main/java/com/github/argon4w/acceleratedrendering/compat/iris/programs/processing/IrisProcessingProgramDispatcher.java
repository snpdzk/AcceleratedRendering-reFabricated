package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class IrisProcessingProgramDispatcher implements IPolygonProgramDispatcher {

    private static final int GROUP_SIZE = 128;

    private final VertexFormat.Mode mode;
    private final ComputeProgram program;
    private final Uniform polygonCountUniform;
    private final Uniform vertexOffsetUniform;

    public IrisProcessingProgramDispatcher(VertexFormat.Mode mode, ComputeProgram program) {
        this.mode = mode;
        this.program = program;
        this.polygonCountUniform = this.program.getUniform("polygonCount");
        this.vertexOffsetUniform = program.getUniform("vertexOffset");
    }

    public IrisProcessingProgramDispatcher(VertexFormat.Mode mode, ResourceLocation key) {
        this(mode, ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public int dispatch(int vertexCount, int vertexOffset) {
        int polygonCount = vertexCount / mode.primitiveLength;

        polygonCountUniform.uploadUnsignedInt(polygonCount);
        vertexOffsetUniform.uploadUnsignedInt(vertexOffset);

        program.useProgram();
        program.dispatch((polygonCount + GROUP_SIZE - 1) / GROUP_SIZE);
        program.resetProgram();

        return program.getBarrierFlags();
    }
}

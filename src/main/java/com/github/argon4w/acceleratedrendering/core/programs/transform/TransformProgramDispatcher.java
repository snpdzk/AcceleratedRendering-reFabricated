package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import net.minecraft.resources.ResourceLocation;

public class TransformProgramDispatcher {

    private static final int GROUP_SIZE = 128;

    private final ComputeProgram program;
    private final Uniform vertexCountUniform;

    private TransformProgramDispatcher(ComputeProgram program) {
        this.program = program;
        this.vertexCountUniform = program.getUniform("vertexCount");
    }

    public TransformProgramDispatcher(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    public void dispatch(int vertexCount) {
        vertexCountUniform.uploadUnsignedInt(vertexCount);
        program.dispatch((vertexCount + GROUP_SIZE - 1) / GROUP_SIZE);
    }
}

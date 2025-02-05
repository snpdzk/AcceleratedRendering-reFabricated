package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class IrisProcessingProgramDispatcher implements IProgramDispatcher {

    private final ComputeProgram program;

    public IrisProcessingProgramDispatcher(ComputeProgram program) {
        this.program = program;
    }

    public IrisProcessingProgramDispatcher(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public void dispatch(VertexFormat.Mode mode, int vertexCount) {
        program.dispatch(vertexCount / mode.primitiveLength);
    }
}

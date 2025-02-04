package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class IrisProcessingProgramDispatcher implements IProgramDispatcher {

    private final ComputeProgram program;
    private final int primitiveLength;

    public IrisProcessingProgramDispatcher(
            ComputeProgram program,
            VertexFormat.Mode mode
    ) {
        this.program = program;
        this.primitiveLength = mode.primitiveLength;
    }

    public IrisProcessingProgramDispatcher(
            ResourceLocation key,
            VertexFormat.Mode mode
    ) {
        this(
                ComputeShaderProgramLoader.getProgram(key),
                mode
        );
    }

    @Override
    public void dispatch(
            VertexFormat.Mode mode,
            ElementBuffer elementBuffer,
            AcceleratedBufferBuilder builder
    ) {
        int count = builder.getVertexCount() / primitiveLength;
        program.dispatch(count, 1, 1);
    }
}

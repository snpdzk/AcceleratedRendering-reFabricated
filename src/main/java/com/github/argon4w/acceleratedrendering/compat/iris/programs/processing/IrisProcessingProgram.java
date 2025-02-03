package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IProcessingProgram;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class IrisProcessingProgram implements IProcessingProgram {

    private final Program program;
    private final int primitiveLength;

    public IrisProcessingProgram(
            Program program,
            VertexFormat.Mode mode
    ) {
        this.program = program;
        this.primitiveLength = mode.primitiveLength;
    }

    public IrisProcessingProgram(
            ResourceLocation key,
            VertexFormat.Mode mode
    ) {
        this(
                ComputeShaderProgramLoader.getProgram(key),
                mode
        );
    }

    @Override
    public int getCount(
            VertexFormat.Mode mode,
            ElementBuffer elementBuffer,
            AcceleratedBufferBuilder builder
    ) {
        return builder.getVertexCount() / primitiveLength;
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

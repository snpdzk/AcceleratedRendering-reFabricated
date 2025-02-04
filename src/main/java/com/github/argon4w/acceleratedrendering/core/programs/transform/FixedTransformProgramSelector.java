package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class FixedTransformProgramSelector implements ITransformProgramSelector {

    private final ITransformProgramSelector parent;
    private final ComputeProgram program;
    private final VertexFormat vertexFormat;

    public FixedTransformProgramSelector(
            ITransformProgramSelector parent,
            VertexFormat vertexFormat,
            ComputeProgram program
    ) {
        this.parent = parent;
        this.program = program;
        this.vertexFormat = vertexFormat;
    }

    public FixedTransformProgramSelector(
            ITransformProgramSelector parent,
            ResourceLocation key,
            VertexFormat vertexFormat
    ) {
        this(
                parent,
                vertexFormat, ComputeShaderProgramLoader.getProgram(key)
        );
    }

    @Override
    public ComputeProgram select(VertexFormat vertexFormat) {
        return this.vertexFormat == vertexFormat
                ? program
                : parent.select(vertexFormat);
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

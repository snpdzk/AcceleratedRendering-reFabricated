package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.mojang.blaze3d.vertex.VertexFormat;

public class ThrowingTransformProgramSelector implements ITransformProgramSelector {

    public static final ThrowingTransformProgramSelector INSTANCE = new ThrowingTransformProgramSelector();

    @Override
    public Program select(VertexFormat vertexFormat) {
        throw new IllegalStateException("Cannot select a valid transform program for vertex format: " + vertexFormat);
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

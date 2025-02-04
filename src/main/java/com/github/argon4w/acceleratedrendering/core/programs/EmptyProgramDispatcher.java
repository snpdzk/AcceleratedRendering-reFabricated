package com.github.argon4w.acceleratedrendering.core.programs;

import com.mojang.blaze3d.vertex.VertexFormat;

public class EmptyProgramDispatcher implements IProgramDispatcher {

    public static final EmptyProgramDispatcher INSTANCE = new EmptyProgramDispatcher();

    @Override
    public void dispatch(VertexFormat.Mode mode, int vertexCount) {

    }
}

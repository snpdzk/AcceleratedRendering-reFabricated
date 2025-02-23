package com.github.argon4w.acceleratedrendering.core.programs;

import com.mojang.blaze3d.vertex.VertexFormat;

public class EmptyProgramDispatcher implements IPolygonProgramDispatcher {

    public static final EmptyProgramDispatcher INSTANCE = new EmptyProgramDispatcher();

    @Override
    public int dispatch(VertexFormat.Mode mode, int vertexCount) {
        return 0;
    }
}

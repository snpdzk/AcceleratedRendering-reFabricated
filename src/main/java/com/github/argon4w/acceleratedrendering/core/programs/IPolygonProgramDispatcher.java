package com.github.argon4w.acceleratedrendering.core.programs;

import com.mojang.blaze3d.vertex.VertexFormat;

public interface IPolygonProgramDispatcher {

    void dispatch(VertexFormat.Mode mode, int vertexCount);
}

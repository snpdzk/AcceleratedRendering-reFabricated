package com.github.argon4w.acceleratedrendering.core.programs.processing;

import com.github.argon4w.acceleratedrendering.core.programs.EmptyProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;

public class EmptyPolygonProcessor implements IPolygonProcessor {

    public static final EmptyPolygonProcessor INSTANCE = new EmptyPolygonProcessor();

    @Override
    public IProgramDispatcher select(VertexFormat.Mode mode) {
        return EmptyProgramDispatcher.INSTANCE;
    }

    @Override
    public void uploadSharings(long address) {

    }

    @Override
    public void uploadVertex(long address) {

    }
}

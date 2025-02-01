package com.github.argon4w.acceleratedrendering.core.programs.processing;

import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.Nullable;

public class EmptyPolygonProcessor implements IPolygonProcessor {

    public static final EmptyPolygonProcessor INSTANCE = new EmptyPolygonProcessor();

    @Override
    public @Nullable IProcessingProgram selectProgram(VertexFormat vertexFormat) {
        return null;
    }

    @Override
    public void uploadSharings(long address) {

    }

    @Override
    public void uploadVertex(long address) {

    }
}

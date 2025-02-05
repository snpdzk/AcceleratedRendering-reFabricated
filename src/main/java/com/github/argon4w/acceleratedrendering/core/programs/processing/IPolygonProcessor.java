package com.github.argon4w.acceleratedrendering.core.programs.processing;

import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.neoforged.fml.ModLoader;

public interface IPolygonProcessor {

    IProgramDispatcher select(VertexFormat.Mode mode);
    void uploadSharings(long address);
    void uploadVertex(long address);

     static IPolygonProcessor empty() {
        return EmptyPolygonProcessor.INSTANCE;
    }

    static IPolygonProcessor get(VertexFormat vertexFormat) {
         return ModLoader.postEventWithReturn(new LoadPolygonProcessorEvent(vertexFormat)).processor;
    }
}

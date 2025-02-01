package com.github.argon4w.acceleratedrendering.core.programs.processing;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.UnaryOperator;

public class LoadPolygonProcessorEvent extends Event implements IModBusEvent {
    private final VertexFormat vertexFormat;

    IPolygonProcessor processor;

    public LoadPolygonProcessorEvent(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        processor = IPolygonProcessor.empty();
    }

    public void loadFor(VertexFormat vertexFormat, UnaryOperator<IPolygonProcessor> selector) {
        if (this.vertexFormat == vertexFormat) {
            this.processor = selector.apply(this.processor);
        }
    }
}

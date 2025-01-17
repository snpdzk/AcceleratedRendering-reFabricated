package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.UnaryOperator;

public class LoadTransformProgramSelectorEvent extends Event implements IModBusEvent {

    private final VertexFormat vertexFormat;

    ITransformProgramSelector selector;

    public LoadTransformProgramSelectorEvent(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        selector = ITransformProgramSelector.throwing();
    }

    public void loadFor(VertexFormat vertexFormat, UnaryOperator<ITransformProgramSelector> selector) {
        if (this.vertexFormat == vertexFormat) {
            this.selector = selector.apply(this.selector);
        }
    }
}

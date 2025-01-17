package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.UnaryOperator;

public class LoadCullingProgramSelectorEvent extends Event implements IModBusEvent {

    private final VertexFormat vertexFormat;

    ICullingProgramSelector selector;

    public LoadCullingProgramSelectorEvent(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        selector = ICullingProgramSelector.passThrough();
    }

    public void loadFor(VertexFormat vertexFormat, UnaryOperator<ICullingProgramSelector> selector) {
        if (this.vertexFormat == vertexFormat) {
            this.selector = selector.apply(this.selector);
        }
    }
}

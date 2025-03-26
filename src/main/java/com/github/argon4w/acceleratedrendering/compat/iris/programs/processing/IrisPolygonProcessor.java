package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.programs.dispatchers.FixedPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.dispatchers.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.extras.IExtraVertexData;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IPolygonProcessor;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class IrisPolygonProcessor implements IPolygonProcessor {

    private final IPolygonProcessor parent;
    private final VertexFormat.Mode mode;
    private final IPolygonProgramDispatcher dispatcher;
    private final IExtraVertexData extraVertexData;

    public IrisPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat vertexFormat,
            VertexFormat.Mode mode,
            ResourceLocation key
    ) {
        this.parent = parent;
        this.mode = mode;
        this.dispatcher = new FixedPolygonProgramDispatcher(mode, key);
        this.extraVertexData = new IrisExtraVertexData(vertexFormat);
    }

    @Override
    public IPolygonProgramDispatcher select(VertexFormat.Mode mode) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.select(mode);
        }

        if (!IrisCompatFeature.isPolygonProcessingEnabled()) {
            return parent.select(mode);
        }

        if (this.mode != mode) {
            return parent.select(mode);
        }

        return dispatcher;
    }

    @Override
    public IExtraVertexData getExtraVertex(VertexFormat.Mode mode) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.getExtraVertex(mode);
        }

        if (!IrisCompatFeature.isPolygonProcessingEnabled()) {
            return parent.getExtraVertex(mode);
        }

        if (this.mode != mode) {
            return parent.getExtraVertex(mode);
        }

        return extraVertexData;
    }
}

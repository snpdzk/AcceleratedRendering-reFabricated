package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IExtraVertexData;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IPolygonProcessor;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryUtil;

public class IrisEntityPolygonProcessor implements IPolygonProcessor {

    private final IPolygonProcessor parent;
    private final VertexFormat.Mode mode;
    private final IPolygonProgramDispatcher dispatcher;
    private final IrisEntityExtraVertexData extraVertexData;

    public IrisEntityPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat vertexFormat,
            VertexFormat.Mode mode,
            IPolygonProgramDispatcher dispatcher
    ) {
        this.parent = parent;
        this.mode = mode;
        this.dispatcher = dispatcher;
        this.extraVertexData = new IrisEntityExtraVertexData(vertexFormat.getOffset(IrisVertexFormats.ENTITY_ID_ELEMENT));
    }

    public IrisEntityPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat vertexFormat,
            VertexFormat.Mode mode,
            ResourceLocation key
    ) {
        this(
                parent,
                vertexFormat,
                mode,
                new IrisProcessingProgramDispatcher(mode, key)
        );
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

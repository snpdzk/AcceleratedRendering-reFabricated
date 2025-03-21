package com.github.argon4w.acceleratedrendering.core.programs.processing;

import com.github.argon4w.acceleratedrendering.core.programs.FixedPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class FixedPolygonProcessor implements IPolygonProcessor {

    private final IPolygonProcessor parent;
    private final VertexFormat.Mode mode;
    private final IPolygonProgramDispatcher dispatcher;

    public FixedPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat.Mode mode,
            IPolygonProgramDispatcher dispatcher
    ) {
        this.parent = parent;
        this.mode = mode;
        this.dispatcher = dispatcher;
    }

    public FixedPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat.Mode mode,
            ResourceLocation key
    ) {
        this(
                parent,
                mode,
                new FixedPolygonProgramDispatcher(mode, key)
        );
    }

    @Override
    public IPolygonProgramDispatcher select(VertexFormat.Mode mode) {
        return this.mode == mode
                ? dispatcher
                : parent.select(mode);
    }

    @Override
    public IExtraVertexData getExtraVertex(VertexFormat.Mode mode) {
        return EmptyExtraVertexData.INSTANCE;
    }
}

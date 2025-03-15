package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class PassThroughCullingProgramSelector implements ICullingProgramSelector {

    public static final ICullingProgramSelector INSTANCE = new PassThroughCullingProgramSelector();

    @Override
    public IPolygonProgramDispatcher select(RenderType renderType) {
        VertexFormat.Mode mode = renderType.mode;

        if (mode == VertexFormat.Mode.QUADS) {
            return PassThroughCullingProgramDispatcher.QUAD;
        }

        if (mode == VertexFormat.Mode.TRIANGLES) {
            return PassThroughCullingProgramDispatcher.TRIANGLE;
        }

        throw new IllegalArgumentException("Unsupported mode: " + mode);
    }

    @Override
    public int getSharingFlags(VertexFormat.Mode mode) {
        return 0;
    }
}

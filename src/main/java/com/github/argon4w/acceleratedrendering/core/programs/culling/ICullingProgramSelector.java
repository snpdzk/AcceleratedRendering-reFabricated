package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.fml.ModLoader;

public interface ICullingProgramSelector {

    IPolygonProgramDispatcher select(RenderType renderType);
    int getSharingFlags();

    static ICullingProgramSelector passThrough() {
        return PassThroughCullingProgramSelector.INSTANCE;
    }

    static ICullingProgramSelector get(VertexFormat vertexFormat) {
        return ModLoader.postEventWithReturn(new LoadCullingProgramSelectorEvent(vertexFormat)).selector;
    }
}

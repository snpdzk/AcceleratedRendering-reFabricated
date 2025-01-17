package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class PassThroughCullingProgramSelector implements ICullingProgramSelector {

    public static final ICullingProgramSelector INSTANCE = new PassThroughCullingProgramSelector();

    @Override
    public ICullingProgram select(RenderType renderType, VertexFormat vertexFormat) {
        return PassThroughCullingProgram.INSTANCE;
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

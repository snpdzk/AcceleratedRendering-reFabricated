package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import net.minecraft.client.renderer.RenderType;

public class PassThroughCullingProgramSelector implements ICullingProgramSelector {

    public static final ICullingProgramSelector INSTANCE = new PassThroughCullingProgramSelector();

    @Override
    public IPolygonProgramDispatcher select(RenderType renderType) {
        return PassThroughCullingProgramDispatcher.INSTANCE;
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import net.minecraft.client.renderer.RenderType;

public class PassThroughCullingProgramSelector implements ICullingProgramSelector {

    public static final ICullingProgramSelector INSTANCE = new PassThroughCullingProgramSelector();

    @Override
    public IProgramDispatcher select(RenderType renderType) {
        return PassThroughCullingProgramDispatcher.INSTANCE;
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

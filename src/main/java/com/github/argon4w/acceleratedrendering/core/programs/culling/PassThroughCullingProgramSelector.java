package com.github.argon4w.acceleratedrendering.core.programs.culling;

import net.minecraft.client.renderer.RenderType;

public class PassThroughCullingProgramSelector implements ICullingProgramSelector {

    public static final ICullingProgramSelector INSTANCE = new PassThroughCullingProgramSelector();

    @Override
    public ICullingProgram select(RenderType renderType) {
        return PassThroughCullingProgram.INSTANCE;
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

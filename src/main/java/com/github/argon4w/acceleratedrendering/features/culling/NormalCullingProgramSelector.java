package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class NormalCullingProgramSelector implements ICullingProgramSelector {

    private final ICullingProgramSelector parent;
    private final IPolygonProgramDispatcher dispatcher;

    public NormalCullingProgramSelector(ICullingProgramSelector parent, IPolygonProgramDispatcher dispatcher) {
        this.parent = parent;
        this.dispatcher = dispatcher;
    }

    public NormalCullingProgramSelector(ICullingProgramSelector parent, ResourceLocation key) {
        this(parent, new NormalCullingProgramDispatcher(key));
    }

    @Override
    public IPolygonProgramDispatcher select(RenderType renderType) {
        if (!NormalCullingFeature.isEnabled()) {
            return parent.select(renderType);
        }

        if (NormalCullingFeature.shouldIgnoreCullState()) {
            return dispatcher;
        }

        if (RenderTypeUtils.isCulled(renderType)) {
            return dispatcher;
        }

        return parent.select(renderType);
    }

    @Override
    public int getSharingFlags() {
        if (!NormalCullingFeature.isEnabled()) {
            return parent.getSharingFlags();
        }

        if (!NormalCullingFeature.shouldCull()) {
            return parent.getSharingFlags() | 0b1;
        }

        return parent.getSharingFlags();
    }
}

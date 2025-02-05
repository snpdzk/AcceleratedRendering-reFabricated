package com.github.argon4w.acceleratedrendering.compat.iris.programs.culling;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.github.argon4w.acceleratedrendering.features.culling.NormalCullingFeature;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class IrisCullingProgramSelector implements ICullingProgramSelector {

    private final ICullingProgramSelector parent;
    private final IProgramDispatcher dispatcher;

    public IrisCullingProgramSelector(ICullingProgramSelector parent, IProgramDispatcher dispatcher) {
        this.parent = parent;
        this.dispatcher = dispatcher;
    }

    public IrisCullingProgramSelector(ICullingProgramSelector parent, ResourceLocation key) {
        this(parent, new IrisCullingProgramDispatcher(key));
    }

    @Override
    public IProgramDispatcher select(RenderType renderType) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.select(renderType);
        }

        if (!IrisCompatFeature.isIrisCompatCullingEnabled()) {
            return parent.select(renderType);
        }

        if (!IrisCompatFeature.isShadowCullingEnabled() && ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            return parent.select(renderType);
        }

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
        if (!IrisCompatFeature.isEnabled()) {
            return parent.getSharingFlags();
        }

        if (!IrisCompatFeature.isIrisCompatCullingEnabled()) {
            return parent.getSharingFlags();
        }

        if (!IrisCompatFeature.isShadowCullingEnabled() && ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            return parent.getSharingFlags();
        }

        if (!NormalCullingFeature.isEnabled()) {
            return parent.getSharingFlags();
        }

        if (!NormalCullingFeature.shouldCull()) {
            return parent.getSharingFlags() | 0b1;
        }

        return parent.getSharingFlags();
    }
}

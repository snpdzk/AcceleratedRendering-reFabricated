package com.github.argon4w.acceleratedrendering.compat.iris.programs.culling;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.github.argon4w.acceleratedrendering.features.culling.NormalCullingFeature;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class IrisCullingProgramSelector implements ICullingProgramSelector {

    private final ICullingProgramSelector parent;
    private final VertexFormat.Mode mode;
    private final IPolygonProgramDispatcher dispatcher;

    public IrisCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat.Mode mode,
            IPolygonProgramDispatcher dispatcher
    ) {
        this.parent = parent;
        this.mode = mode;
        this.dispatcher = dispatcher;
    }

    public IrisCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat.Mode mode,
            ResourceLocation key
    ) {
        this(
                parent,
                mode,
                new IrisCullingProgramDispatcher(mode, key)
        );
    }

    @Override
    public IPolygonProgramDispatcher select(RenderType renderType) {
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

        if (this.mode != renderType.mode) {
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
    public int getFlags(VertexFormat.Mode mode) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.getFlags(mode);
        }

        if (!IrisCompatFeature.isIrisCompatCullingEnabled()) {
            return parent.getFlags(mode);
        }

        if (!NormalCullingFeature.isEnabled()) {
            return parent.getFlags(mode);
        }

        if (this.mode != mode) {
            return parent.getFlags(mode);
        }

        if (!IrisCompatFeature.isShadowCullingEnabled() && ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
            return 0b0;
        }

        if (!NormalCullingFeature.shouldCull()) {
            return 0b1;
        }

        return 0;
    }
}

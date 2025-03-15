package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class NormalCullingProgramSelector implements ICullingProgramSelector {

    private final ICullingProgramSelector parent;
    private final VertexFormat.Mode mode;
    private final IPolygonProgramDispatcher dispatcher;

    public NormalCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat.Mode mode,
            IPolygonProgramDispatcher dispatcher
    ) {
        this.parent = parent;
        this.mode = mode;
        this.dispatcher = dispatcher;
    }

    public NormalCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat.Mode mode,
            ResourceLocation key
    ) {
        this(
                parent,
                mode,
                new NormalCullingProgramDispatcher(mode, key)
        );
    }

    @Override
    public IPolygonProgramDispatcher select(RenderType renderType) {
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
    public int getSharingFlags(VertexFormat.Mode mode) {
        if (!NormalCullingFeature.isEnabled()) {
            return parent.getSharingFlags(mode);
        }

        if (this.mode != mode) {
            return parent.getSharingFlags(mode);
        }

        if (!NormalCullingFeature.shouldCull()) {
            return 0b1;
        }

        return 0b0;
    }
}

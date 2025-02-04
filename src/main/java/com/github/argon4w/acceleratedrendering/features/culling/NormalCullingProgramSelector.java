package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class NormalCullingProgramSelector implements ICullingProgramSelector {

    private final ICullingProgramSelector parent;
    private final VertexFormat vertexFormat;
    private final IProgramDispatcher dispatcher;

    public NormalCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat vertexFormat,
            IProgramDispatcher dispatcher
    ) {
        this.parent = parent;
        this.vertexFormat = vertexFormat;
        this.dispatcher = dispatcher;
    }

    public NormalCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat vertexFormat,
            ResourceLocation key
    ) {
        this(
                parent,
                vertexFormat,
                new NormalCullingProgramDispatcher(key)
        );
    }

    @Override
    public IProgramDispatcher select(RenderType renderType) {
        if (!NormalCullingFeature.isEnabled()) {
            return parent.select(renderType);
        }

        if (this.vertexFormat != renderType.format) {
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
            return 0b1;
        }

        return 0;
    }
}

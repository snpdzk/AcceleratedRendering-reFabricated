package com.github.argon4w.acceleratedrendering.compat.iris.programs;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgram;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.github.argon4w.acceleratedrendering.features.culling.NormalCullingFeature;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class IrisCullingProgramSelector implements ICullingProgramSelector {

    private final ICullingProgramSelector parent;
    private final VertexFormat vertexFormat;
    private final ICullingProgram program;

    public IrisCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat vertexFormat,
            ICullingProgram program
    ) {
        this.parent = parent;
        this.vertexFormat = vertexFormat;
        this.program = program;
    }

    public IrisCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat vertexFormat,
            ResourceLocation key
    ) {
        this(
                parent,
                vertexFormat,
                new IrisCullingProgram(key)
        );
    }

    @Override
    public ICullingProgram select(RenderType renderType, VertexFormat vertexFormat) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.select(renderType, vertexFormat);
        }

        if (!IrisCompatFeature.isIrisCompatCullingEnabled()) {
            return parent.select(renderType, vertexFormat);
        }

        if (!NormalCullingFeature.isEnabled()) {
            return parent.select(renderType, vertexFormat);
        }

        if (this.vertexFormat != vertexFormat) {
            return parent.select(renderType, vertexFormat);
        }

        if (NormalCullingFeature.shouldIgnoreCullState()) {
            return program;
        }

        if (RenderTypeUtils.isCulled(renderType)) {
            return program;
        }

        return parent.select(renderType, vertexFormat);
    }

    @Override
    public int getSharingFlags() {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.getSharingFlags();
        }

        if (!IrisCompatFeature.isIrisCompatCullingEnabled()) {
            return parent.getSharingFlags();
        }

        if (!NormalCullingFeature.isEnabled()) {
            return parent.getSharingFlags();
        }

        if (!IrisCompatFeature.isIrisCompatCullingEnabled()) {
            return parent.getSharingFlags();
        }

        if (!NormalCullingFeature.shouldCull()) {
            return 0b1;
        }

        return 0;
    }
}

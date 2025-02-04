package com.github.argon4w.acceleratedrendering.compat.iris.programs.transform;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.transform.ITransformProgramSelector;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.resources.ResourceLocation;

public class IrisTransformProgramSelector implements ITransformProgramSelector {

    private final ITransformProgramSelector parent;
    private final VertexFormat vertexFormat;
    private final ComputeProgram program;

    public IrisTransformProgramSelector(
            ITransformProgramSelector parent,
            VertexFormat vertexFormat,
            ComputeProgram program
    ) {
        this.parent = parent;
        this.vertexFormat = vertexFormat;
        this.program = program;
    }

    public IrisTransformProgramSelector(
            ITransformProgramSelector parent,
            VertexFormat vertexFormat,
            ResourceLocation key
    ) {
        this(
                parent,
                vertexFormat,
                ComputeShaderProgramLoader.getProgram(key)
        );
    }

    @Override
    public ComputeProgram select(VertexFormat vertexFormat) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.select(vertexFormat);
        }

        if (this.vertexFormat != vertexFormat) {
            return parent.select(vertexFormat);
        }

        if (!WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
            return parent.select(vertexFormat);
        }

        if (!ImmediateState.isRenderingLevel) {
            return parent.select(vertexFormat);
        }

        return program;
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

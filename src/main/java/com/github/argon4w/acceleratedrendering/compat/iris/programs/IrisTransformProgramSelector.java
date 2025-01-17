package com.github.argon4w.acceleratedrendering.compat.iris.programs;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.transform.ITransformProgramSelector;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.resources.ResourceLocation;

public class IrisTransformProgramSelector implements ITransformProgramSelector {

    private final ITransformProgramSelector parent;
    private final VertexFormat vertexFormat;
    private final Program program;

    public IrisTransformProgramSelector(
            ITransformProgramSelector parent,
            VertexFormat vertexFormat,
            Program program
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
    public Program select(VertexFormat vertexFormat) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.select(vertexFormat);
        }

        if (this.vertexFormat != vertexFormat) {
            return parent.select(vertexFormat);
        }

        if (!WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
            return parent.select(vertexFormat);
        }

        return program;
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

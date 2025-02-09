package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class NormalCullingProgramDispatcher implements IPolygonProgramDispatcher {

    private static final int GROUP_SIZE = 128;

    private final ComputeProgram program;
    private final Uniform viewMatrixUniform;
    private final Uniform polygonCountUniform;

    private NormalCullingProgramDispatcher(ComputeProgram program) {
        this.program = program;
        this.viewMatrixUniform = this.program.getUniform("viewMatrix");
        this.polygonCountUniform = this.program.getUniform("polygonCount");
    }

    public NormalCullingProgramDispatcher(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public void dispatch(VertexFormat.Mode mode, int vertexCount) {
        int polygonCount = mode.indexCount(vertexCount) / 3;

        viewMatrixUniform.uploadMatrix4f(RenderSystem.getModelViewMatrix());
        polygonCountUniform.uploadUnsignedInt(polygonCount);
        program.dispatch((polygonCount + GROUP_SIZE - 1) / GROUP_SIZE);
    }
}

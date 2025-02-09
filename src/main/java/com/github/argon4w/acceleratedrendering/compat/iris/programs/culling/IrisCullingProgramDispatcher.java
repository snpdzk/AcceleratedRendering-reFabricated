package com.github.argon4w.acceleratedrendering.compat.iris.programs.culling;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class IrisCullingProgramDispatcher implements IPolygonProgramDispatcher {

    private static final int GROUP_SIZE = 128;

    private final ComputeProgram program;
    private final Uniform viewMatrixUniform;
    private final Uniform polygonCountUniform;

    private IrisCullingProgramDispatcher(ComputeProgram program) {
        this.program = program;
        this.viewMatrixUniform = program.getUniform("viewMatrix");
        this.polygonCountUniform = program.getUniform("polygonCount");
    }

    public IrisCullingProgramDispatcher(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public void dispatch(VertexFormat.Mode mode, int vertexCount) {
        int polygonCount = mode.indexCount(vertexCount) / 3;

        viewMatrixUniform.uploadMatrix4f(getModelViewMatrix());
        polygonCountUniform.uploadUnsignedInt(polygonCount);
        program.dispatch((polygonCount + GROUP_SIZE - 1) / GROUP_SIZE);
    }

    private Matrix4f getModelViewMatrix() {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered()
                ? ShadowRenderer.MODELVIEW
                : RenderSystem.getModelViewMatrix();
    }
}

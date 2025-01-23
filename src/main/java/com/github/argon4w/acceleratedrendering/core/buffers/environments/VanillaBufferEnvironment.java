package com.github.argon4w.acceleratedrendering.core.buffers.environments;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.meshes.ServerMesh;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgram;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.transform.ITransformProgramSelector;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;

public class VanillaBufferEnvironment implements IBufferEnvironment {

    private final VertexFormat vertexFormat;

    private final ITransformProgramSelector transformProgramSelector;
    private final ICullingProgramSelector cullingProgramSelector;

    public VanillaBufferEnvironment(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;

        this.transformProgramSelector = ITransformProgramSelector.get(vertexFormat);
        this.cullingProgramSelector = ICullingProgramSelector.get(vertexFormat);
    }

    @Override
    public void setupBufferState() {
        vertexFormat.setupBufferState();
    }

    @Override
    public IServerBuffer getServerMeshBuffer() {
        return ServerMesh.Builder.INSTANCE.getStorageBuffer(vertexFormat);
    }

    @Override
    public Program selectTransformProgram() {
        return transformProgramSelector.select(vertexFormat);
    }

    @Override
    public ICullingProgram selectCullProgram(RenderType renderType) {
        return cullingProgramSelector.select(renderType);
    }

    @Override
    public VertexFormat getVertexFormat(RenderType renderType) {
        return renderType.format;
    }

    @Override
    public boolean isAccelerated(VertexFormat vertexFormat) {
        return this.vertexFormat == vertexFormat;
    }

    @Override
    public int getOffset(VertexFormatElement element) {
        return vertexFormat.getOffset(element);
    }

    @Override
    public int getSharingFlags() {
        return transformProgramSelector.getSharingFlags()
                | cullingProgramSelector.getSharingFlags();
    }

    @Override
    public int getVertexSize() {
        return vertexFormat.getVertexSize();
    }
}

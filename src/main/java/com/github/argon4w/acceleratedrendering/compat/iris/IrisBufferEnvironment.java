package com.github.argon4w.acceleratedrendering.compat.iris;

import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.meshes.ServerMesh;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgram;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IPolygonProcessor;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IProcessingProgram;
import com.github.argon4w.acceleratedrendering.core.programs.transform.ITransformProgramSelector;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.ImmediateState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.minecraft.client.renderer.RenderType;

public class IrisBufferEnvironment implements IBufferEnvironment {

    public static final IBufferEnvironment ENTITY = new IrisBufferEnvironment(
            DefaultVertexFormat.NEW_ENTITY,
            IrisVertexFormats.ENTITY
    );

    private final VertexFormat vertexFormat;
    private final VertexFormat irisVertexFormat;

    private final ITransformProgramSelector transformProgramSelector;
    private final ICullingProgramSelector cullingProgramSelector;
    private final IPolygonProcessor polygonProcessor;

    public IrisBufferEnvironment(VertexFormat vertexFormat, VertexFormat irisVertexFormat) {
        this.vertexFormat = vertexFormat;
        this.irisVertexFormat = irisVertexFormat;

        this.transformProgramSelector = ITransformProgramSelector.get(this.vertexFormat);
        this.cullingProgramSelector = ICullingProgramSelector.get(this.vertexFormat);
        this.polygonProcessor = IPolygonProcessor.get(this.vertexFormat);
    }

    private boolean isExtended() {
        return WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()
                && ImmediateState.isRenderingLevel;
    }

    private VertexFormat getCurrentActiveVertexFormat() {
        return isExtended()
                ? irisVertexFormat
                : vertexFormat;
    }

    @Override
    public void setupBufferState() {
        getCurrentActiveVertexFormat().setupBufferState();
    }

    @Override
    public void uploadSharings(long address) {
        polygonProcessor.uploadSharings(address);
    }

    @Override
    public void uploadVertex(long address) {
        polygonProcessor.uploadVertex(address);
    }

    @Override
    public IServerBuffer getServerMeshBuffer() {
        return ServerMesh.Builder.INSTANCE.getStorageBuffer(getCurrentActiveVertexFormat());
    }

    @Override
    public Program selectTransformProgram() {
        return transformProgramSelector.select(getCurrentActiveVertexFormat());
    }

    @Override
    public ICullingProgram selectCullProgram(RenderType renderType) {
        return cullingProgramSelector.select(renderType);
    }

    @Override
    public IProcessingProgram selectProcessingProgram() {
        return polygonProcessor.selectProgram(getCurrentActiveVertexFormat());
    }

    @Override
    public VertexFormat getVertexFormat(RenderType renderType) {
        return (isExtended() && renderType.format == vertexFormat)
                ? irisVertexFormat
                : renderType.format;
    }

    @Override
    public boolean isAccelerated(VertexFormat vertexFormat) {
        return (this.vertexFormat == vertexFormat)
                || (isExtended() && irisVertexFormat == vertexFormat);
    }

    @Override
    public int getOffset(VertexFormatElement element) {
        return getCurrentActiveVertexFormat().getOffset(element);
    }

    @Override
    public int getSharingFlags() {
        return transformProgramSelector.getSharingFlags()
                | cullingProgramSelector.getSharingFlags();
    }

    @Override
    public int getVertexSize() {
        return getCurrentActiveVertexFormat().getVertexSize();
    }
}

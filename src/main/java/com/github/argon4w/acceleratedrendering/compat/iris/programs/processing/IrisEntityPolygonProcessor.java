package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IPolygonProcessor;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IProcessingProgram;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.ImmediateState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class IrisEntityPolygonProcessor implements IPolygonProcessor {

    private final IPolygonProcessor parent;
    private final VertexFormat vertexFormat;
    private final int entityOffset;

    public IrisEntityPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat vertexFormat
    ) {
        this.parent = parent;
        this.vertexFormat = vertexFormat;
        this.entityOffset = this.vertexFormat.getOffset(IrisVertexFormats.ENTITY_ID_ELEMENT);
    }

    @Override
    public @Nullable IProcessingProgram selectProgram(VertexFormat vertexFormat) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.selectProgram(vertexFormat);
        }

        if (this.vertexFormat != vertexFormat) {
            return parent.selectProgram(vertexFormat);
        }

        if (!WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
            return parent.selectProgram(vertexFormat);
        }

        if (!ImmediateState.isRenderingLevel) {
            return parent.selectProgram(vertexFormat);
        }

        return null;
    }

    @Override
    public void uploadVertex(long address) {
        uploadSharings(address + entityOffset);
    }

    @Override
    public void uploadSharings(long address) {
        parent.uploadSharings(address);

        if (!IrisCompatFeature.isEnabled()) {
            return;
        }

        if (!IrisCompatFeature.isPolygonProcessingEnabled()) {
            return;
        }

        if (!WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
            return;
        }

        if (!ImmediateState.isRenderingLevel) {
            return;
        }

        MemoryUtil.memPutShort(address + 0L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedEntity());
        MemoryUtil.memPutShort(address + 2L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity());
        MemoryUtil.memPutShort(address + 4L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedItem());
    }
}

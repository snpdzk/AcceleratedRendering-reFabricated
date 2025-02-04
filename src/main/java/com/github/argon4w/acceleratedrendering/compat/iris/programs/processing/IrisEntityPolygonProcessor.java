package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IPolygonProcessor;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.ImmediateState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryUtil;

public class IrisEntityPolygonProcessor implements IPolygonProcessor {

    private final IPolygonProcessor parent;
    private final VertexFormat vertexFormat;
    private final VertexFormat.Mode mode;
    private final IProgramDispatcher dispatcher;
    private final int entityOffset;

    public IrisEntityPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat vertexFormat,
            VertexFormat.Mode mode,
            IProgramDispatcher dispatcher
    ) {
        this.parent = parent;
        this.vertexFormat = vertexFormat;
        this.mode = mode;
        this.dispatcher = dispatcher;
        this.entityOffset = this.vertexFormat.getOffset(IrisVertexFormats.ENTITY_ID_ELEMENT);
    }

    public IrisEntityPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat vertexFormat,
            VertexFormat.Mode mode,
            ResourceLocation key
    ) {
        this(
                parent,
                vertexFormat,
                mode,
                new IrisProcessingProgramDispatcher(key, mode)
        );
    }

    @Override
    public IProgramDispatcher selectDispatcher(
            VertexFormat vertexFormat,
            VertexFormat.Mode mode
    ) {
        if (!WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
            return parent.selectDispatcher(vertexFormat, mode);
        }

        if (!ImmediateState.isRenderingLevel) {
            return parent.selectDispatcher(vertexFormat, mode);
        }

        if (!IrisCompatFeature.isEnabled()) {
            return parent.selectDispatcher(vertexFormat, mode);
        }

        if (this.vertexFormat != vertexFormat) {
            return parent.selectDispatcher(vertexFormat, mode);
        }

        if (this.mode != mode) {
            return parent.selectDispatcher(vertexFormat, mode);
        }

        return dispatcher;
    }

    @Override
    public void uploadVertex(long address) {
        uploadSharings(address + entityOffset);
    }

    @Override
    public void uploadSharings(long address) {
        parent.uploadSharings(address);

        if (!WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()) {
            return;
        }

        if (!ImmediateState.isRenderingLevel) {
            return;
        }

        if (!IrisCompatFeature.isEnabled()) {
            return;
        }

        if (!IrisCompatFeature.isPolygonProcessingEnabled()) {
            return;
        }

        MemoryUtil.memPutShort(address + 0L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedEntity());
        MemoryUtil.memPutShort(address + 2L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity());
        MemoryUtil.memPutShort(address + 4L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedItem());
    }
}

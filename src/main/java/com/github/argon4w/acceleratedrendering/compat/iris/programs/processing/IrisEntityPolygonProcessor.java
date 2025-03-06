package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.compat.iris.programs.IrisPrograms;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IPolygonProcessor;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryUtil;

public class IrisEntityPolygonProcessor implements IPolygonProcessor {

    private final IPolygonProcessor parent;
    private final VertexFormat.Mode mode;
    private final IPolygonProgramDispatcher dispatcher;
    private final int entityOffset;

    public IrisEntityPolygonProcessor(
            IPolygonProcessor parent,
            VertexFormat vertexFormat,
            VertexFormat.Mode mode,
            IPolygonProgramDispatcher dispatcher
    ) {
        this.parent = parent;
        this.mode = mode;
        this.dispatcher = dispatcher;
        this.entityOffset = vertexFormat.getOffset(IrisVertexFormats.ENTITY_ID_ELEMENT);
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
                new IrisProcessingProgramDispatcher(key)
        );
    }

    @Override
    public IPolygonProgramDispatcher select(VertexFormat.Mode mode) {
        if (!IrisCompatFeature.isEnabled()) {
            return parent.select(mode);
        }

        if (!IrisCompatFeature.isPolygonProcessingEnabled()) {
            return parent.select(mode);
        }

        if (this.mode != mode) {
            return parent.select(mode);
        }

        return dispatcher;
    }

    @Override
    public long addExtraVertex(long address) {
        long result = parent.addExtraVertex(address);

        if (!IrisCompatFeature.isEnabled()) {
             return result;
        }

        if (!IrisCompatFeature.isPolygonProcessingEnabled()) {
            return result;
        }

        if ((result & IrisPrograms.ENTITY_ID_BIT) > 0) {
            return result;
        }

        MemoryUtil.memPutShort(address + entityOffset + 0L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedEntity());
        MemoryUtil.memPutShort(address + entityOffset + 2L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity());
        MemoryUtil.memPutShort(address + entityOffset + 4L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedItem());

        return result | IrisPrograms.ENTITY_ID_BIT;
    }
}

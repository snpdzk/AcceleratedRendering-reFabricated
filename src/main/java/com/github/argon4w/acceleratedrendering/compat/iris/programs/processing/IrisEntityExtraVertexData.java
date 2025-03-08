package com.github.argon4w.acceleratedrendering.compat.iris.programs.processing;

import com.github.argon4w.acceleratedrendering.core.programs.processing.IExtraVertexData;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.lwjgl.system.MemoryUtil;

public class IrisEntityExtraVertexData implements IExtraVertexData {

    private final int entityOffset;

    public IrisEntityExtraVertexData(int entityOffset) {
        this.entityOffset = entityOffset;
    }

    @Override
    public void addExtraVertex(long address) {
        MemoryUtil.memPutShort(address + entityOffset + 0L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedEntity());
        MemoryUtil.memPutShort(address + entityOffset + 2L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity());
        MemoryUtil.memPutShort(address + entityOffset + 4L, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedItem());
    }

    @Override
    public void addExtraVarying(long address) {

    }
}

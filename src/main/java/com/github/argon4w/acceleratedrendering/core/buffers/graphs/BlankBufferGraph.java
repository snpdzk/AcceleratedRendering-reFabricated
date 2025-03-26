package com.github.argon4w.acceleratedrendering.core.buffers.graphs;

import net.minecraft.client.renderer.RenderType;

public class BlankBufferGraph implements IBufferGraph {

    private final RenderType renderType;

    public BlankBufferGraph(RenderType renderType) {
        this.renderType = renderType;
    }

    @Override
    public float mapU(float u) {
        return u;
    }

    @Override
    public float mapV(float v) {
        return v;
    }

    @Override
    public int hashCode() {
        return renderType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        return renderType.equals(((BlankBufferGraph) obj).renderType);
    }
}

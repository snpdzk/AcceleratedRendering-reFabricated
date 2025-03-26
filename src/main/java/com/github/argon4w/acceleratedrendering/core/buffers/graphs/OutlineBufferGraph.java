package com.github.argon4w.acceleratedrendering.core.buffers.graphs;

import java.util.Objects;

public class OutlineBufferGraph implements IBufferGraph {

    private final IBufferGraph parent;
    private final int color;

    public OutlineBufferGraph(IBufferGraph parent, int color) {
        this.parent = parent;
        this.color = color;
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
        return Objects.hash(parent, color);
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

        OutlineBufferGraph that = (OutlineBufferGraph) obj;

        return Objects.equals(parent, that.parent)
                && Objects.equals(color, that.color);
    }
}

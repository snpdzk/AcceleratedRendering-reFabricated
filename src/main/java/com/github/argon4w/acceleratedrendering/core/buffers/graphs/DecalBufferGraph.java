package com.github.argon4w.acceleratedrendering.core.buffers.graphs;

import com.github.argon4w.acceleratedrendering.core.utils.MatrixUtils;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

import java.util.Objects;

public class DecalBufferGraph implements IBufferGraph {

    private final IBufferGraph parent;
    private final Matrix4f localTransform;

    public DecalBufferGraph(IBufferGraph parent, Matrix4f localTransform) {
        this.parent = parent;
        this.localTransform = localTransform;
    }

    @Override
    public float mapU(float u) {
        return parent.mapU(u);
    }

    @Override
    public float mapV(float v) {
        return parent.mapV(v);
    }

    @Override
    public RenderType getRenderType() {
        return parent.getRenderType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, localTransform);
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

        DecalBufferGraph that = (DecalBufferGraph) obj;

        return Objects.equals(parent, that.parent)
                && MatrixUtils.equals(localTransform, that.localTransform, 1e-5f);
    }
}

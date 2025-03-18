package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import net.minecraft.client.renderer.RenderType;

public interface IMesh {

    void write(IAcceleratedVertexConsumer extension, int color, int light, int overlay);

    interface Builder {

        MeshCollector newMeshCollector(RenderType key);
        IMesh build(MeshCollector collector);
    }
}

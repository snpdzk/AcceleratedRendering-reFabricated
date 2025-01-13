package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IVertexConsumerExtension;
import net.minecraft.client.renderer.RenderType;

public interface IMesh {
    void render(IVertexConsumerExtension extension, int color, int light, int overlay);

    interface Builder {
        MeshCollector newMeshCollector(RenderType renderType);
        IMesh build(MeshCollector collector);
    }
}

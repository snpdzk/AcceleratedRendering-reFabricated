package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IVertexConsumerExtension;

public class EmptyMesh implements IMesh {

    public static final EmptyMesh INSTANCE = new EmptyMesh();

    @Override
    public void write(
            IVertexConsumerExtension extension,
            int color,
            int light,
            int overlay
    ) {

    }
}

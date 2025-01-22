package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.ElementBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;

public interface ICullingProgram {

    int getCount(VertexFormat.Mode mode, ElementBuffer elementBuffer, AcceleratedBufferBuilder builder);
    void uploadUniforms();
    void useProgram();
    void resetProgram();
}

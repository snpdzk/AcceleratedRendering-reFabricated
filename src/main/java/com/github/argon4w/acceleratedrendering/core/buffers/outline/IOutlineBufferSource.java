package com.github.argon4w.acceleratedrendering.core.buffers.outline;

import net.minecraft.client.renderer.MultiBufferSource;

public interface IOutlineBufferSource extends MultiBufferSource {

    void setColor(int color);
}

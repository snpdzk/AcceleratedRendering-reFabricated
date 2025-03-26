package com.github.argon4w.acceleratedrendering.compat.iris;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;

public class IrisCompatBuffers {

    public static final AcceleratedBufferSource BLOCK_SHADOW = new AcceleratedBufferSource(IBufferEnvironment.Presets.BLOCK);
    public static final AcceleratedBufferSource ENTITY_SHADOW = new AcceleratedBufferSource(IBufferEnvironment.Presets.ENTITY);
    public static final AcceleratedBufferSource GLYPH_SHADOW = new AcceleratedBufferSource(IBufferEnvironment.Presets.POS_COLOR_TEX_LIGHT);
    public static final AcceleratedBufferSource POS_TEX_SHADOW = new AcceleratedBufferSource(IBufferEnvironment.Presets.POS_TEX);
}

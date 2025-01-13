package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL46.*;

public class CommandBuffer extends ImmutableBuffer {

    public CommandBuffer() {
        super(5L * 4L, GL_DYNAMIC_STORAGE_BIT | GL_MAP_WRITE_BIT);
        long address = map(GL_WRITE_ONLY);

        MemoryUtil.memPutInt(address + 0L * 4L, 0);
        MemoryUtil.memPutInt(address + 1L * 4L, 1);
        MemoryUtil.memPutInt(address + 2L * 4L, 0);
        MemoryUtil.memPutInt(address + 3L * 4L, 0);
        MemoryUtil.memPutInt(address + 4L * 4L, 0);

        unmap();
    }
}

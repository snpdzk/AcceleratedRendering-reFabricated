package com.github.argon4w.acceleratedrendering.core.gl;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;

import static org.lwjgl.opengl.GL46.*;

public class GlobalFenceSync {

    public static final GlobalFenceSync INSTANCE = new GlobalFenceSync();

    private final LongArrayFIFOQueue fenceQueue;

    public GlobalFenceSync() {
        this.fenceQueue = new LongArrayFIFOQueue();
    }

    public void fenceSync() {
        fenceQueue.enqueue(glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0));
    }

    public void clientWaitSync() {
        while (fenceQueue.size() > AcceleratedRenderingModEntry.getCpuRenderAheadLimit()) {
            long sync = fenceQueue.dequeueLong();
            glClientWaitSync(sync, GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE);
            glDeleteSync(sync);
        }
    }
}

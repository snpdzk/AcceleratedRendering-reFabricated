package com.github.argon4w.acceleratedrendering.gl;

import static org.lwjgl.opengl.GL46.*;

public class GLSingleFenceSync {

    private long sync;

    public GLSingleFenceSync() {
        this.sync = -1;
    }

    public void fenceSync() {
        sync = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);;
    }

    public void clientWaitSync() {
        if (sync != -1) {
            glClientWaitSync(sync, GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE);
            glDeleteSync(sync);
        }
    }
}

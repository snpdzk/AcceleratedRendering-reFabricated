package com.github.argon4w.acceleratedrendering.core.gl.buffers;

public interface IServerBufferSegment extends IServerBuffer {

    long getOffset();
    IServerBuffer getParent();
}

package com.github.argon4w.acceleratedrendering.core.gl.buffers;

public interface IServerBuffer {

    int getBufferHandle();
    void bind(int target);
    void bindBase(int target, int index);
}

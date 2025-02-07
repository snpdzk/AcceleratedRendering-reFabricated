package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import java.nio.ByteBuffer;

public interface IClientBuffer {

    long reserve(long bytes);
    ByteBuffer asByteBuffer();
}

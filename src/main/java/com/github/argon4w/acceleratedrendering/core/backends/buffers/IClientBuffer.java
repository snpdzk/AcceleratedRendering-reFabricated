package com.github.argon4w.acceleratedrendering.core.backends.buffers;

import java.nio.ByteBuffer;

public interface IClientBuffer {

    long reserve(long bytes);
}

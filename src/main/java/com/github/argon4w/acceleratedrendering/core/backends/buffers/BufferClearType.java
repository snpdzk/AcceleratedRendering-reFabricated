package com.github.argon4w.acceleratedrendering.core.backends.buffers;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public enum BufferClearType {
    BYTE(
            GL_R8UI,
            GL_RED,
            GL_UNSIGNED_BYTE
    ),
    INTEGER(
            GL_R32UI,
            GL_RED_INTEGER,
            GL_UNSIGNED_INT
    );

    private final int internalFormat;
    private final int format;
    private final int type;

    BufferClearType(
            int internalFormat,
            int format,
            int type
    ) {
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
    }

    public void clear(
            int bufferHandle,
            long offset,
            long size,
            ByteBuffer buffer
    ) {
        glClearNamedBufferSubData(
                bufferHandle,
                internalFormat,
                offset,
                size,
                format,
                type,
                buffer
        );
    }
}

package com.github.argon4w.acceleratedrendering.core.gl.buffers;

public class MutableBuffer implements IServerBuffer {

    private final int bits;

    protected long bufferSize;
    protected ImmutableBuffer glBuffer;

    public MutableBuffer(long initialSize, int bits) {
        this.bits = bits;
        this.bufferSize = initialSize;
        this.glBuffer = new ImmutableBuffer(initialSize, bits);
    }

    public void resize(long atLeast) {
        long newBufferSize = bufferSize;

        while (newBufferSize < atLeast) {
            newBufferSize *= 2;
        }

        resizeTo(newBufferSize);
    }

    public void resizeTo(long newBufferSize) {
        if (newBufferSize <= bufferSize) {
            return;
        }

        ImmutableBuffer newBuffer = new ImmutableBuffer(newBufferSize, bits);

        glBuffer.copyTo(newBuffer, bufferSize);
        glBuffer.delete();

        glBuffer = newBuffer;
        bufferSize = newBufferSize;
    }

    public long map(int flags) {
        return glBuffer.map(bufferSize, flags);
    }

    public void unmap() {
        glBuffer.unmap();
    }

    public void delete() {
        glBuffer.delete();
    }

    public long getBufferSize() {
        return bufferSize;
    }

    @Override
    public void bind(int target) {
        glBuffer.bind(target);
    }

    @Override
    public void bindBase(int target, int index) {
        glBuffer.bindBase(target, index);
    }

    @Override
    public int getBufferHandle() {
        return glBuffer.getBufferHandle();
    }
}

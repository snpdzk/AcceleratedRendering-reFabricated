package com.github.argon4w.acceleratedrendering.core.gl.buffers;

public class MutableBuffer implements IServerBuffer {

    private final int bits;

    protected boolean resized;
    protected long bufferSize;
    protected ImmutableBuffer glBuffer;

    public MutableBuffer(long initialSize, int bits) {
        this.bits = bits;
        this.resized = false;
        this.bufferSize = initialSize;
        this.glBuffer = new ImmutableBuffer(initialSize, bits);
        this.glBuffer.clearBytes(0L, this.bufferSize);
    }

    public void expand(long bytes) {
        if (bytes <= 0) {
            return;
        }

        beforeExpand();

        long newSize = bufferSize + bytes;
        ImmutableBuffer newBuffer = new ImmutableBuffer(newSize, bits);

        newBuffer.clearBytes(0L, newSize);
        glBuffer.copyTo(newBuffer, bufferSize);
        glBuffer.delete();

        onExpand(bytes);

        resized = true;
        glBuffer = newBuffer;
        bufferSize = newSize;

        afterExpand();
    }

    public void beforeExpand() {

    }

    public void onExpand(long bytes) {

    }

    public void afterExpand() {

    }

    public void resize(long atLeast) {
        resizeTo(Long.highestOneBit(atLeast) << 1);
    }

    public void resizeTo(long newBufferSize) {
        expand(newBufferSize - bufferSize);
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

    public boolean isResized() {
        return resized;
    }

    public void resetResized() {
        resized = false;
    }

    @Override
    public int getBufferHandle() {
        return glBuffer.getBufferHandle();
    }

    @Override
    public void bind(int target) {
        glBuffer.bind(target);
    }

    @Override
    public void clearInteger(long offset, int value) {
        glBuffer.clearInteger(offset, value);
    }

    @Override
    public void clearBytes(long offset, long size) {
        glBuffer.clearBytes(offset, size);
    }

    @Override
    public void subData(long offset, int[] data) {
        glBuffer.subData(offset, data);
    }

    @Override
    public void bindBase(int target, int index) {
        glBuffer.bindBase(target, index);
    }

    @Override
    public void bindRange(
            int target,
            int index,
            long offset,
            long size
    ) {
        glBuffer.bindRange(
                target,
                index,
                offset,
                size
        );
    }
}

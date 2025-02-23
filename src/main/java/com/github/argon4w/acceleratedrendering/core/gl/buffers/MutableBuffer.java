package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import java.nio.ByteBuffer;

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
    }

    public void expand(long bytes) {
        if (bytes <= 0) {
            return;
        }

        beforeExpand();

        ImmutableBuffer newBuffer = new ImmutableBuffer(bufferSize + bytes, bits);

        glBuffer.copyTo(newBuffer, bufferSize);
        glBuffer.delete();

        onExpand(bytes);

        resized = true;
        glBuffer = newBuffer;
        bufferSize += bytes;

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
    public void subData(long offset, int[] data) {
        glBuffer.subData(offset, data);
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

    @Override
    public void clear(
            long offset,
            long size,
            ByteBuffer buffer
    ) {
        glBuffer.clear(
                offset,
                size,
                buffer
        );
    }

    @Override
    public void clear(
            long offset,
            long size,
            int value
    ) {
        glBuffer.clear(
                offset,
                size,
                value
        );
    }

    @Override
    public int getBufferHandle() {
        return glBuffer.getBufferHandle();
    }
}

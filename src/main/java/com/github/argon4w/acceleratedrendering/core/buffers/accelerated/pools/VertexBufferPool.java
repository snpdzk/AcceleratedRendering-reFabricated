package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools;

import com.github.argon4w.acceleratedrendering.core.backends.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.backends.buffers.SegmentBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSetPool;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleResetPool;
import org.apache.commons.lang3.mutable.MutableLong;

public class VertexBufferPool extends SimpleResetPool<VertexBufferPool.VertexBuffer, Void> {

    private final AcceleratedBufferSetPool.BufferSet bufferSet;
    private final SegmentBuffer vertexBufferOut;
    private final MutableLong vertexBufferSize;

    public VertexBufferPool(int size, AcceleratedBufferSetPool.BufferSet bufferSet) {
        super(size, null);

        this.bufferSet = bufferSet;
        this.vertexBufferOut = new SegmentBuffer(64L * size, size);
        this.vertexBufferSize = new MutableLong(0L);
    }

    public void prepare() {
        vertexBufferOut.resizeTo(vertexBufferSize.getValue());
        vertexBufferOut.clearSegment();
    }

    public void bind(int target) {
        vertexBufferOut.bind(target);
    }

    public void bindBase(int target, int index) {
        vertexBufferOut.bindBase(target, index);
    }

    public void resetResized() {
        vertexBufferOut.resetResized();
    }

    public boolean isResized() {
        return vertexBufferOut.isResized();
    }

    @Override
    public void delete() {
        vertexBufferOut.delete();
        super.delete();
    }

    @Override
    protected VertexBuffer create(Void context, int i) {
        return new VertexBuffer();
    }

    @Override
    protected void reset(VertexBuffer vertexBuffer) {
        vertexBuffer.poolReset();
    }

    @Override
    protected void delete(VertexBuffer vertexBuffer) {
        vertexBuffer.poolDelete();
    }

    public class VertexBuffer extends MappedBuffer {

        private long offset;

        public VertexBuffer() {
            super(64L);
            this.offset = -1;
        }

        @Override
        public void onExpand(long bytes) {
            vertexBufferSize.add(bytes);
        }

        @Override
        public void delete() {
            throw new IllegalStateException("Pooled buffers cannot be deleted directly.");
        }

        @Override
        public void reset() {
            throw new IllegalStateException("Pooled buffers cannot be reset directly.");
        }

        private void poolDelete() {
            super.delete();
        }

        private void poolReset() {
            super.reset();
            this.offset = -1;
        }

        @Override
        public int getOffset() {
            if (offset == -1) {
                offset = vertexBufferOut.getSegmentOffset(position / bufferSet.getVertexSize());
            }

            return (int) offset;
        }
    }
}

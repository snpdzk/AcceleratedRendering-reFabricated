package com.github.argon4w.acceleratedrendering.core.gl.buffers;

import com.github.argon4w.acceleratedrendering.core.utils.SimpleResetPool;
import org.apache.commons.lang3.mutable.MutableLong;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class SegmentBuffer extends MutableBuffer {

    private final MutableLong segmentOffset;
    private final SegmentPool segmentPool;

    public SegmentBuffer(long initialSize, int segments) {
        super(initialSize, GL_DYNAMIC_STORAGE_BIT);

        this.segmentOffset = new MutableLong(0);
        this.segmentPool = new SegmentPool(segments);
    }

    @Override
    public void delete() {
        super.delete();
        segmentPool.delete();
    }

    public void clearSegment() {
        segmentOffset.setValue(0);
        segmentPool.reset();
    }

    public Segment getSegment(long size) {
        return segmentPool.get(size);
    }

    public class SegmentPool extends SimpleResetPool<Segment, Void> {

        public SegmentPool(int size) {
            super(size, null);
        }

        public Segment get(long size) {
            return get().set(segmentOffset.getAndAdd(size), size);
        }

        @Override
        protected Segment create(Void context) {
            return new Segment();
        }

        @Override
        protected void reset(Segment segment) {
            segment.reset();
        }

        @Override
        protected void delete(Segment segment) {
            segment.reset();
        }

        @Override
        public Segment fail() {
            throw new IllegalStateException("Segment pool is empty.");
        }
    }

    public class Segment implements IServerBufferSegment {

        private long offset;
        private long size;

        private Segment() {
            this.offset = -1;
            this.size = -1;
        }

        public void reset() {
            this.offset = -1;
            this.size = -1;
        }

        public Segment set(long offset, long size) {
            this.offset = offset;
            this.size = size;

            return this;
        }

        @Override
        public long getOffset() {
            return offset;
        }

        @Override
        public IServerBuffer getParent() {
            return SegmentBuffer.this;
        }

        @Override
        public int getBufferHandle() {
            return SegmentBuffer.this.getBufferHandle();
        }

        @Override
        public void subData(long offset, int[] data) {
            SegmentBuffer.this.subData(offset + this.offset, data);
        }

        @Override
        public void bind(int target) {
            SegmentBuffer.this.bind(target);
        }

        @Override
        public void bindBase(int target, int index) {
            SegmentBuffer.this.bindRange(
                    target,
                    index,
                    offset,
                    size
            );
        }

        @Override
        public void bindRange(
                int target,
                int index,
                long offset,
                long size
        ) {
            SegmentBuffer.this.bindRange(
                    target,
                    index,
                    this.offset + offset,
                    size
            );
        }

        @Override
        public void clear(
                long offset,
                long size,
                ByteBuffer buffer
        ) {
            SegmentBuffer.this.clear(
                    offset + this.offset,
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
            SegmentBuffer.this.clear(
                    offset + this.offset,
                    size,
                    value
            );
        }
    }
}

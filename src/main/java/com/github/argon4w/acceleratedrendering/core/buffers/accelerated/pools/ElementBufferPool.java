package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.SegmentBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.MutableSize;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleResetPool;
import org.apache.commons.lang3.mutable.MutableLong;

import static org.lwjgl.opengl.GL46.GL_ELEMENT_ARRAY_BUFFER;

public class ElementBufferPool extends SimpleResetPool<ElementBufferPool.ElementSegment, Void> {

    private final SegmentBuffer elementBufferOut;
    private final MutableLong elementBufferOutSize;

    public ElementBufferPool(int size) {
        super(size, null);

        this.elementBufferOut = new SegmentBuffer(64L * size, size);
        this.elementBufferOutSize = new MutableLong(64L * size);
    }

    public void prepare() {
        elementBufferOut.resizeTo(elementBufferOutSize.getValue());
        elementBufferOut.clearSegment();
    }

    public void bindElementBuffer() {
        elementBufferOut.bind(GL_ELEMENT_ARRAY_BUFFER);
    }

    public void resetResized() {
        elementBufferOut.resetResized();
    }

    public boolean isResized() {
        return elementBufferOut.isResized();
    }

    @Override
    public void delete() {
        elementBufferOut.delete();
        super.delete();
    }

    @Override
    protected ElementSegment create(Void value, int i) {
        return new ElementSegment();
    }

    @Override
    protected void reset(ElementSegment elementSegment) {
        elementSegment.reset();
    }

    @Override
    protected void delete(ElementSegment elementSegment) {

    }

    public class ElementSegment extends MutableSize {

        private long elementBytes;

        public ElementSegment() {
            super(64L);
            this.elementBytes = 0L;
        }

        @Override
        public void onExpand(long bytes) {
            elementBufferOutSize.add(bytes);
        }

        public IServerBuffer getBuffer() {
            return elementBufferOut.getSegment(size);
        }

        private void reset() {
            elementBytes = 0L;
        }

        public void countPolygons(int count) {
            elementBytes += count * 4L;

            if (elementBytes > size) {
                resize(elementBytes);
            }
        }
    }
}

package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBufferSegment;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.SegmentBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleResetPool;
import org.apache.commons.lang3.mutable.MutableLong;

import static org.lwjgl.opengl.GL46.*;

public class ElementBufferPool extends SimpleResetPool<ElementBufferPool.ElementBuffer, Void> {

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
    protected ElementBuffer create(Void value) {
        return new ElementBuffer();
    }

    @Override
    protected void reset(ElementBuffer elementBuffer) {
        elementBuffer.reset();
    }

    @Override
    protected void delete(ElementBuffer elementBuffer) {
        elementBuffer.poolDelete();
    }

    public class ElementBuffer extends MappedBuffer {

        public ElementBuffer() {
            super(64L);
        }

        @Override
        public void onExpand(long bytes) {
            elementBufferOutSize.add(bytes);
        }

        @Override
        public void delete() {
            throw new IllegalStateException("Pooled buffer cannot be deleted directly.");
        }

        public IServerBufferSegment getSegmentOut() {
            return elementBufferOut.getSegment(bufferSize);
        }

        private void poolDelete() {
            super.delete();
        }
    }
}

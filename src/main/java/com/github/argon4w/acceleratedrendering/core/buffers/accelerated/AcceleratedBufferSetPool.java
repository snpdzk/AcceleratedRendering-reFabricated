package com.github.argon4w.acceleratedrendering.core.buffers.accelerated;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.backends.Sync;
import com.github.argon4w.acceleratedrendering.core.backends.VertexArray;
import com.github.argon4w.acceleratedrendering.core.backends.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.DrawContextPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.ElementBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.MappedBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.VertexBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.programs.extras.IExtraVertexData;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.apache.commons.lang3.mutable.MutableInt;

import static org.lwjgl.opengl.GL46.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL46.GL_SHADER_STORAGE_BUFFER;

public class AcceleratedBufferSetPool {

    private final IBufferEnvironment bufferEnvironment;
    private final BufferSet[] bufferSets;
    private final int size;

    public AcceleratedBufferSetPool(IBufferEnvironment bufferEnvironment) {
        this.bufferEnvironment = bufferEnvironment;
        this.size = CoreFeature.getPooledBufferSetSize();
        this.bufferSets = new BufferSet[this.size];

        for (int i = 0; i < this.size; i++) {
            this.bufferSets[i] = new BufferSet();
        }
    }

    public BufferSet getBufferSet() {
        for (int i = 0; i < size; i++) {
            BufferSet buffer = bufferSets[i];

            if (buffer.isFree()) {
                buffer.setUsed();
                return buffer;
            }
        }

        BufferSet bufferSet = bufferSets[0];
        bufferSet.waitSync();
        bufferSet.setUsed();

        return bufferSet;
    }

    public class BufferSet {

        private final int size;
        private final DrawContextPool drawContextPool;
        private final ElementBufferPool elementBufferPool;
        private final MappedBuffer sharingBuffer;
        private final MappedBufferPool varyingBuffer;
        private final VertexBufferPool vertexBuffer;
        private final VertexArray vertexArray;
        private final Sync sync;
        private final MutableInt sharing;

        private boolean used;
        private VertexFormat format;

        public BufferSet() {
            this.size = CoreFeature.getPooledElementBufferSize();
            this.drawContextPool = new DrawContextPool(this.size);
            this.elementBufferPool = new ElementBufferPool(this.size);
            this.sharingBuffer = new MappedBuffer(64L);
            this.varyingBuffer = new MappedBufferPool(this.size);
            this.vertexBuffer = new VertexBufferPool(this.size, this);
            this.vertexArray = new VertexArray();
            this.sync = new Sync();
            this.sharing = new MutableInt(0);

            this.used = false;
            this.format = null;
        }

        public void reset() {
            drawContextPool.reset();
            elementBufferPool.reset();
            varyingBuffer.reset();
            sharingBuffer.reset();
            vertexBuffer.reset();

            sharing.setValue(0);
        }

        public void bindTransformBuffers() {
            vertexBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 1);
            sharingBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 2);
            bufferEnvironment.getServerMeshBuffer().bindBase(GL_SHADER_STORAGE_BUFFER, 4);
        }

        public void bindDrawBuffers() {
            if (format != bufferEnvironment.getActiveFormat()
                    || elementBufferPool.isResized()
                    || vertexBuffer.isResized()) {
                format = bufferEnvironment.getActiveFormat();
                elementBufferPool.bindElementBuffer();
                elementBufferPool.resetResized();
                vertexBuffer.bind(GL_ARRAY_BUFFER);
                vertexBuffer.resetResized();
                bufferEnvironment.setupBufferState();
            }

            drawContextPool.bindCommandBuffer();
        }

        public void prepare() {
            sharingBuffer.flush();
            vertexBuffer.prepare();
            elementBufferPool.prepare();
        }

        public VertexBufferPool.VertexBuffer getVertexBuffer() {
            return vertexBuffer.get();
        }

        public MappedBufferPool.Pooled getVaryingBuffer() {
            return varyingBuffer.get();
        }

        public ElementBufferPool.ElementSegment getElementSegment() {
            return elementBufferPool.get();
        }

        public DrawContextPool.IndirectDrawContext getDrawContext() {
            return drawContextPool.get();
        }

        public int getOffset(VertexFormatElement element) {
            return bufferEnvironment.getOffset(element);
        }

        public long getVertexSize() {
            return bufferEnvironment.getVertexSize();
        }

        public int getSharing() {
            return sharing.getAndIncrement();
        }

        public long reserveSharing() {
            return sharingBuffer.reserve(4L * 4L * 4L + 4L * 4L * 3L);
        }

        public IExtraVertexData getExtraVertex(VertexFormat.Mode mode) {
            return bufferEnvironment.getExtraVertex(mode);
        }

        public void bindVertexArray() {
            vertexArray.bindVertexArray();
        }

        public void resetVertexArray() {
            vertexArray.unbindVertexArray();
        }

        public int getSize() {
            return size;
        }

        public void setUsed() {
            used = true;
        }

        public void setInFlight() {
            used = false;
            sync.setSync();
        }

        protected void waitSync() {
            if (!sync.isSyncSet()) {
                return;
            }

            sync.waitSync();
            sync.resetSync();
        }

        public boolean isFree() {
            if (used) {
                return false;
            }

            if (!sync.isSyncSet()) {
                return true;
            }

            if (!sync.isSyncSignaled()) {
                return false;
            }

            sync.resetSync();

            return true;
        }
    }
}

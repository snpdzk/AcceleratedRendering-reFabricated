package com.github.argon4w.acceleratedrendering.core.buffers.accelerated;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.pools.MappedBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.gl.Sync;
import com.github.argon4w.acceleratedrendering.core.gl.VertexArray;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.ImmutableBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MutableBuffer;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import static org.lwjgl.opengl.GL46.*;

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

        private final MappedBufferPool elementBufferPool;
        private final MappedBuffer sharingBuffer;
        private final MappedBuffer varyingBuffer;
        private final MappedBuffer vertexBufferIn;
        private final MutableBuffer vertexBufferOut;
        private final MutableBuffer elementBufferOut;
        private final ImmutableBuffer commandBuffer;
        private final VertexArray vertexArray;
        private final Sync sync;

        private int sharing;
        private int element;
        private boolean used;

        public BufferSet() {
            this.elementBufferPool = new MappedBufferPool(CoreFeature.getPooledElementBufferSize());
            this.sharingBuffer = new MappedBuffer(64L);
            this.varyingBuffer = new MappedBuffer(64L);
            this.vertexBufferIn = new MappedBuffer(64L);
            this.vertexBufferOut = new MutableBuffer(64L, GL_DYNAMIC_STORAGE_BIT);
            this.elementBufferOut = new MutableBuffer(64L, GL_DYNAMIC_STORAGE_BIT);
            this.commandBuffer = new ImmutableBuffer(GL_DYNAMIC_STORAGE_BIT, new int[] {0, 1, 0, 0, 0});
            this.vertexArray = new VertexArray();
            this.sync = new Sync();

            this.sharing = -1;
            this.element = 0;
            this.used = false;
        }

        public void reset() {
            elementBufferPool.reset();
            varyingBuffer.reset();
            sharingBuffer.reset();
            vertexBufferIn.reset();

            sharing = -1;
            element = 0;
        }

        public void bindTransformBuffers() {
            vertexBufferOut.resizeTo(vertexBufferIn.getBufferSize());
            vertexBufferIn.bindBase(GL_SHADER_STORAGE_BUFFER, 0);
            vertexBufferOut.bindBase(GL_SHADER_STORAGE_BUFFER, 1);
            sharingBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 2);
            varyingBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 3);
            bufferEnvironment.getServerMeshBuffer().bindBase(GL_SHADER_STORAGE_BUFFER, 4);
        }

        public void bindCullingBuffers(MappedBuffer elementBufferIn) {
            elementBufferOut.resizeTo(elementBufferIn.getBufferSize());
            commandBuffer.bindBase(GL_ATOMIC_COUNTER_BUFFER, 0);
            elementBufferIn.bindBase(GL_SHADER_STORAGE_BUFFER, 5);
            elementBufferOut.bindBase(GL_SHADER_STORAGE_BUFFER, 6);
        }

        public void bindDrawBuffers() {
            vertexBufferOut.bind(GL_ARRAY_BUFFER);
            elementBufferOut.bind(GL_ELEMENT_ARRAY_BUFFER);
            commandBuffer.bind(GL_DRAW_INDIRECT_BUFFER);
        }

        public int getElement(int count) {
            int element = this.element;
            this.element += count;

            return element;
        }

        public MappedBuffer getElementBuffer() {
            return elementBufferPool.get();
        }

        public int getOffset(VertexFormatElement element) {
            return bufferEnvironment.getOffset(element);
        }

        public int getVertexSize() {
            return bufferEnvironment.getVertexSize();
        }

        public int getSharingFlags() {
            return bufferEnvironment.getSharingFlags();
        }

        public int getSharing() {
            return ++ this.sharing;
        }

        public int getVertexCount() {
            return (int) vertexBufferIn.getBufferSize() / bufferEnvironment.getVertexSize();
        }

        public long reserveSharing() {
            return sharingBuffer.reserve(4L * 4L * 4L + 4L * 4L * 3L + 4L * 4L);
        }

        public long reserveVertex() {
            return vertexBufferIn.reserve(bufferEnvironment.getVertexSize());
        }

        public long reservePolygons(long count) {
            return vertexBufferIn.reserve(bufferEnvironment.getVertexSize() * count);
        }

        public long reserveVarying() {
            return varyingBuffer.reserve(5L * 4L);
        }

        public long reserveVaryings(long count) {
            return varyingBuffer.reserve(5L * 4L * count);
        }

        public void addExtraSharings(long address) {
            bufferEnvironment.addExtraSharings(address);
        }

        public void addExtraVertex(long address) {
            bufferEnvironment.addExtraVertex(address);
        }

        public void bindVertexArray() {
            vertexArray.bindVertexArray();
        }

        public void resetVertexArray() {
            vertexArray.unbindVertexArray();
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

package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.CommandBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MutableBuffer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class AcceleratedBufferSetPool {

    private final IBufferEnvironment bufferEnvironment;
    private final ArrayList<BufferSet> buffers;

    public AcceleratedBufferSetPool(IBufferEnvironment bufferEnvironment) {
        this.bufferEnvironment = bufferEnvironment;
        this.buffers = new ArrayList<>();
    }

    public BufferSet getBufferSet() {
        for (int i = 0; i < buffers.size(); i++) {
            BufferSet buffer = buffers.get(i);

            if (buffer.isFree()) {
                buffer.setUsed();
                return buffer;
            }
        }

        if (buffers.size() < AcceleratedRenderingModEntry.getMaximumPooledBuffers()) {
            BufferSet bufferSet = new BufferSet();
            buffers.add(bufferSet);

            return bufferSet;
        }

        BufferSet bufferSet = buffers.getFirst();
        bufferSet.waitSync();
        bufferSet.setUsed();

        return bufferSet;
    }

    public class BufferSet {

        private final Map<RenderType, ElementBuffer> elementBuffers;

        private final MappedBuffer sharingBuffer;
        private final MappedBuffer varyingBuffer;
        private final MappedBuffer vertexBufferIn;
        private final MutableBuffer vertexBufferOut;
        private final MutableBuffer indexBufferOut;
        private final CommandBuffer commandBuffer;
        private final IntBuffer holder;

        private final int vaoHandle;

        private int sharing;
        private int element;

        private boolean used;
        private long sync;

        public BufferSet() {
            this.elementBuffers = new Object2ObjectLinkedOpenHashMap<>();

            this.sharingBuffer = new MappedBuffer(1024L);
            this.varyingBuffer = new MappedBuffer(1024L);
            this.vertexBufferIn = new MappedBuffer(1024L);
            this.vertexBufferOut = new MutableBuffer(1024L, GL_DYNAMIC_STORAGE_BIT);
            this.indexBufferOut = new MutableBuffer(1024L, GL_DYNAMIC_STORAGE_BIT);
            this.commandBuffer = new CommandBuffer();
            this.holder = MemoryUtil.memCallocInt(1);

            this.vaoHandle = glCreateVertexArrays();

            this.sharing = -1;
            this.element = 0;

            this.used = true;
            this.sync = -1;
        }

        public void reset() {
            for (ElementBuffer elementBuffer : elementBuffers.values()) {
                elementBuffer.reset();
            }

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
        }

        public void bindCullingBuffers(long indexSize) {
            indexBufferOut.resizeTo(indexSize);
            commandBuffer.bindBase(GL_ATOMIC_COUNTER_BUFFER, 0);
            indexBufferOut.bindBase(GL_SHADER_STORAGE_BUFFER, 6);
        }

        public void bindDrawBuffers() {
            vertexBufferOut.bind(GL_ARRAY_BUFFER);
            indexBufferOut.bind(GL_ELEMENT_ARRAY_BUFFER);
            commandBuffer.bind(GL_DRAW_INDIRECT_BUFFER);
        }

        public ElementBuffer getElementBuffer(RenderType renderType) {
            ElementBuffer elementBuffer = elementBuffers.get(renderType);

            if (elementBuffer == null) {
                elementBuffer = new ElementBuffer(renderType.mode, this);
                elementBuffers.put(renderType, elementBuffer);
            }

            return elementBuffer;
        }

        public int getElement(int count) {
            int element = this.element;
            this.element += count;

            return element;
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

        public void bindVertexArray() {
            glBindVertexArray(vaoHandle);
        }

        public void resetVertexArray() {
            glBindVertexArray(0);
        }

        public void setUsed() {
            used = true;
        }

        public void setInFlight() {
            used = false;
            sync = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        }

        protected void waitSync() {
            if (sync == -1) {
                return;
            }

            glClientWaitSync(sync, GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE);
            glDeleteSync(sync);

            sync = -1;
        }

        public boolean isFree() {
            if (used) {
                return false;
            }

            if (sync == -1) {
                return true;
            }

            int result = glGetSynci(sync, GL_SYNC_STATUS, holder);

            if (result == GL_UNSIGNALED) {
                return false;
            }

            glDeleteSync(sync);
            sync = -1;

            return true;
        }
    }
}

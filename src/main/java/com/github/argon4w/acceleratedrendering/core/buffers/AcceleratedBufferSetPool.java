package com.github.argon4w.acceleratedrendering.core.buffers;

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

        if (buffers.size() < 10) {
            BufferSet bufferSet = new BufferSet();
            buffers.add(bufferSet);

            return bufferSet;
        }

        BufferSet bufferSet = buffers.getFirst();
        bufferSet.forceWait();
        bufferSet.setUsed();

        return bufferSet;
    }

    public class BufferSet {

        private final Map<RenderType, IAcceleratedBuffers> acceleratedBuffers;

        private final MappedBuffer sharingBuffer;
        private final MappedBuffer varyingBuffer;
        private final MappedBuffer vertexBufferIn;
        private final MutableBuffer vertexBufferOut;
        private final MutableBuffer indexBufferOut;
        private final CommandBuffer commandBuffer;
        private final IntBuffer holder;

        private final int vaoHandle;

        private boolean used;
        private long sync;

        public BufferSet() {
            this.acceleratedBuffers = new Object2ObjectLinkedOpenHashMap<>();

            this.sharingBuffer = new MappedBuffer(1024L);
            this.varyingBuffer = new MappedBuffer(1024L);
            this.vertexBufferIn = new MappedBuffer(1024L);
            this.vertexBufferOut = new MutableBuffer(1024L, GL_DYNAMIC_STORAGE_BIT);
            this.indexBufferOut = new MutableBuffer(1024L, GL_DYNAMIC_STORAGE_BIT);
            this.commandBuffer = new CommandBuffer();
            this.holder = MemoryUtil.memCallocInt(1);

            this.vaoHandle = glCreateVertexArrays();

            this.used = true;
            this.sync = -1;
        }

        public void resetInputBuffers() {
            varyingBuffer.reset();
            sharingBuffer.reset();
            vertexBufferIn.reset();
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

        public int getVertexCount() {
            return (int) vertexBufferIn.getBufferSize() / bufferEnvironment.getVertexSize();
        }

        public void bindVertexArray() {
            glBindVertexArray(vaoHandle);
        }

        public void resetVertexArray() {
            glBindVertexArray(0);
        }

        public Map<RenderType, IAcceleratedBuffers> getAcceleratedBuffers() {
            return acceleratedBuffers;
        }

        public MappedBuffer getSharingBuffer() {
            return sharingBuffer;
        }

        public MappedBuffer getVaryingBuffer() {
            return varyingBuffer;
        }

        public MappedBuffer getVertexBufferIn() {
            return vertexBufferIn;
        }

        public void setUsed() {
            used = true;
        }

        public void setInFlight() {
            used = false;
            sync = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        }

        protected void forceWait() {
            if (sync != -1) {
                glClientWaitSync(sync, GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE);
                glDeleteSync(sync);

                sync = -1;
            }
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

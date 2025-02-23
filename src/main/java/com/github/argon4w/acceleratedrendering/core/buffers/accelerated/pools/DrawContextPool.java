package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBufferSegment;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.SegmentBuffer;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleResetPool;
import com.mojang.blaze3d.vertex.VertexFormat;

import static org.lwjgl.opengl.GL46.*;

public class DrawContextPool extends SimpleResetPool<DrawContextPool.IndirectDrawContext, SegmentBuffer> {

    public DrawContextPool(int size) {
        super(size, new SegmentBuffer(20L * size, size));
    }

    public void bindCommandBuffer() {
        getContext().bind(GL_DRAW_INDIRECT_BUFFER);
    }

    @Override
    protected IndirectDrawContext create(SegmentBuffer buffer) {
        return new IndirectDrawContext(buffer.getSegment(20L));
    }

    @Override
    protected void reset(IndirectDrawContext drawContext) {

    }

    @Override
    protected void delete(IndirectDrawContext drawContext) {

    }

    @Override
    public void delete() {
        super.getContext().delete();
    }

    public static void waitBarriers() {
        glMemoryBarrier(GL_ELEMENT_ARRAY_BARRIER_BIT | GL_COMMAND_BARRIER_BIT);
    }

    public static class IndirectDrawContext {

        private final long commandOffset;
        private final IServerBuffer commandBuffer;

        private int cachedOffset;

        public IndirectDrawContext(IServerBufferSegment commandBuffer) {
            this.commandOffset = commandBuffer.getOffset();
            this.commandBuffer = commandBuffer;
            this.commandBuffer.subData(0, new int[] {0, 1, 0, 0, 0});

            this.cachedOffset = -1;
        }

        public void bindComputeBuffers(ElementBufferPool.ElementBuffer elementBufferIn) {
            IServerBufferSegment elementBufferOut = elementBufferIn.getSegmentOut();
            int elementOffset = (int) elementBufferOut.getOffset();

            if (cachedOffset != elementOffset) {
                cachedOffset = elementOffset;
                commandBuffer.clear(8, 4, elementOffset / 4);
            }

            commandBuffer.clear(0, 4, null);
            commandBuffer.bindBase(GL_ATOMIC_COUNTER_BUFFER, 0);

            elementBufferIn.bindBase(GL_SHADER_STORAGE_BUFFER, 5);
            elementBufferOut.bindBase(GL_SHADER_STORAGE_BUFFER, 6);
        }

        public void drawElements(VertexFormat.Mode mode) {
            glDrawElementsIndirect(
                    mode.asGLMode,
                    GL_UNSIGNED_INT,
                    commandOffset
            );
        }
    }
}

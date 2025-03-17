package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

import static org.lwjgl.opengl.GL46.*;

public class TransformProgramDispatcher {

    private static final int GROUP_SIZE = 128;

    private final ComputeProgram program;
    private final Uniform vertexCountUniform;
    private final Uniform vertexOffsetUniform;

    private TransformProgramDispatcher(ComputeProgram program) {
        this.program = program;
        this.vertexCountUniform = program.getUniform("vertexCount");
        this.vertexOffsetUniform = program.getUniform("vertexOffset");
    }

    public TransformProgramDispatcher(ResourceLocation key) {
        this(ComputeShaderProgramLoader.getProgram(key));
    }

    public void dispatch(Collection<AcceleratedBufferBuilder> builders) {
        program.useProgram();

        for (AcceleratedBufferBuilder builder : builders) {
            int vertexCount = builder.getVertexCount();
            MappedBuffer vertexBuffer = builder.getVertexBuffer();
            MappedBuffer varyingBuffer = builder.getVaryingBuffer();

            vertexBuffer.flush();
            varyingBuffer.flush();

            vertexBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 0);
            varyingBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 3);

            vertexCountUniform.uploadUnsignedInt(vertexCount);
            vertexOffsetUniform.uploadUnsignedInt(builder.getVertexOffset());

            program.dispatch((vertexCount + GROUP_SIZE - 1) / GROUP_SIZE);
        }

        program.resetProgram();
        program.waitBarriers();
    }
}

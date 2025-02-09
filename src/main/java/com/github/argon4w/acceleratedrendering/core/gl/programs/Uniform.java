package com.github.argon4w.acceleratedrendering.core.gl.programs;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL41.glProgramUniform1ui;
import static org.lwjgl.opengl.GL46.glProgramUniformMatrix4fv;

public class Uniform {

    private final int programHandle;
    private final int uniformLocation;

    public Uniform(int programHandle, int uniformLocation) {
        this.programHandle = programHandle;
        this.uniformLocation = uniformLocation;
    }

    public void uploadMatrix4fv(Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrixBuffer = stack.mallocFloat(16);
            matrix.get(matrixBuffer);
            glProgramUniformMatrix4fv(programHandle, uniformLocation, false, matrixBuffer);
        }
    }

    public void upload1ui(int value) {
        glProgramUniform1ui(programHandle, uniformLocation, value);
    }
}

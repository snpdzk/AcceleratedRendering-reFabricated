package com.github.argon4w.acceleratedrendering.core.gl.programs;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.glProgramUniformMatrix4fv;

public class Uniform {

    private final int programHandle;
    private final int uniformLocation;
    private final FloatBuffer matrixBuffer;

    public Uniform(int programHandle, int uniformLocation) {
        this.programHandle = programHandle;
        this.uniformLocation = uniformLocation;
        this.matrixBuffer = MemoryUtil.memCallocFloat(16);
    }

    public void upload(Matrix4f matrix) {
        glProgramUniformMatrix4fv(programHandle, uniformLocation, false, matrix.get(matrixBuffer));
    }

    public void delete() {
        MemoryUtil.memFree(matrixBuffer);
    }
}

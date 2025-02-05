package com.github.argon4w.acceleratedrendering.core.gl.programs;

import static org.lwjgl.opengl.GL46.*;

public class ComputeProgram {

    private final int programHandle;
    private final int barrierFlags;

    private int lastProgram;
    private boolean used;

    public ComputeProgram(int barrierFlags) {
        this.programHandle = glCreateProgram();
        this.barrierFlags = barrierFlags;
        this.lastProgram = 0;
        this.used = false;
    }

    public void dispatch(int count) {
        dispatch(
                count,
                1,
                1
        );
    }

    public void dispatch(
            int x,
            int y,
            int z
    ) {
        useProgram();
        glDispatchCompute(x, y, z);
        glMemoryBarrier(barrierFlags);
        resetProgram();
    }

    public void useProgram() {
        if (!used) {
            lastProgram = glGetInteger(GL_CURRENT_PROGRAM);
            glUseProgram(programHandle);
            used = true;
        }
    }

    public void resetProgram() {
        if (used) {
            glUseProgram(lastProgram);
            lastProgram = 0;
            used = false;
        }
    }

    public void attachShader(ComputeShader computeShader) {
        glAttachShader(programHandle, computeShader.getShaderHandle());
    }

    public boolean linkProgram() {
        glLinkProgram(programHandle);
        return glGetProgrami(programHandle, GL_LINK_STATUS) == GL_TRUE;
    }

    public int getUniformLocation(String name) {
        return glGetUniformLocation(programHandle, name);
    }

    public Uniform getUniform(String name) {
        return new Uniform(programHandle, getUniformLocation(name));
    }

    public String getInfoLog() {
        return glGetProgramInfoLog(programHandle);
    }

    public int getProgramHandle() {
        return programHandle;
    }

    public boolean isUsed() {
        return used;
    }

    public void delete() {
        glDeleteProgram(programHandle);
    }
}

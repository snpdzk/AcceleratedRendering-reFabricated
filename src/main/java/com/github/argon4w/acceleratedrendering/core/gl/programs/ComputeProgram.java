package com.github.argon4w.acceleratedrendering.core.gl.programs;

import static org.lwjgl.opengl.GL46.*;

public class ComputeProgram {

    private final int programHandle;
    private final int barrierFlags;

    public ComputeProgram(int barrierFlags) {
        this.programHandle = glCreateProgram();
        this.barrierFlags = barrierFlags;
    }

    public void dispatch(int count) {
        int lastProgram = glGetInteger(GL_CURRENT_PROGRAM);

        glUseProgram(programHandle);
        glDispatchCompute(count, 1, 1);
        glMemoryBarrier(barrierFlags);
        glUseProgram(lastProgram);
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

    public void delete() {
        glDeleteProgram(programHandle);
    }
}

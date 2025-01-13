package com.github.argon4w.acceleratedrendering.core.gl.programs;

import static org.lwjgl.opengl.GL46.*;

public class Program {

    private final int programHandle;

    private int lastProgram;
    private boolean used;

    public Program() {
        this.programHandle = glCreateProgram();
        this.lastProgram = 0;
        this.used = false;
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

    public void attachShader(Shader shader) {
        glAttachShader(programHandle, shader.getShaderHandle());
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
}

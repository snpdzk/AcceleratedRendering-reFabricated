package com.github.argon4w.acceleratedrendering.core.gl.programs;

import static org.lwjgl.opengl.GL46.*;

public class Shader {

    private final int shaderHandle;

    public Shader(int type) {
        this.shaderHandle = glCreateShader(type);
    }

    public void setShaderSource(String source) {
        glShaderSource(shaderHandle, source);
    }

    public boolean compileShader() {
        glCompileShader(shaderHandle);
        return glGetShaderi(shaderHandle, GL_COMPILE_STATUS) == GL_TRUE;
    }

    public String getInfoLog() {
        return glGetShaderInfoLog(shaderHandle);
    }

    public void delete() {
        glDeleteShader(shaderHandle);
    }

    public int getShaderHandle() {
        return shaderHandle;
    }
}

package com.github.argon4w.acceleratedrendering.utils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class GLUtils {
    public static int newShader() {
        return glCreateShader(GL_COMPUTE_SHADER);
    }

    public static int newProgram() {
        return glCreateProgram();
    }

    public static void setShaderSource(int shader, String shaderSource) {
        glShaderSource(shader, shaderSource);
    }

    public static void attachShader(int program, int shader) {
        glAttachShader(program, shader);
    }

    public static void compileShader(int shader) {
        glCompileShader(shader);
    }

    public static void linkProgram(int program) {
        glLinkProgram(program);
    }

    public static boolean isShaderCompiled(int shader) {
        return glGetShaderi(shader, GL_COMPILE_STATUS) == GL_TRUE;
    }

    public static boolean isProgramLinked(int program) {
        return glGetProgrami(program, GL_LINK_STATUS) == GL_TRUE;
    }

    public static String getShaderInfoLog(int shader) {
        return glGetShaderInfoLog(shader);
    }

    public static String getProgramInfoLog(int shader) {
        return glGetProgramInfoLog(shader);
    }

    public static void deleteShader(int shader) {
        glDeleteShader(shader);
    }

    public static int getCurrentProgram() {
        return glGetInteger(GL_CURRENT_PROGRAM);
    }

    public static void useProgram(int program) {
        glUseProgram(program);
    }

    public static void waitForShaderStorage() {
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
    }

    public static void dispatchCompute(int count) {
        glDispatchCompute(count, 1, 1);
    }

    public static int newBuffer() {
        return glCreateBuffers();
    }

    public static void setBufferStorage(int buffer, long size) {
        glNamedBufferStorage(buffer, size, GL_DYNAMIC_STORAGE_BIT);
    }

    public static void setBufferData(int buffer, ByteBuffer value) {
        glNamedBufferSubData(buffer, 0, value);
    }

    public static void bindShaderStorage(int buffer, int base) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, base, buffer);
    }

    public static long fenceSync() {
        return glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

    public static void clientWaitSync(long sync) {
        glClientWaitSync(sync, GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE);
    }

    public static void deleteSync(long sync) {
        glDeleteSync(sync);
    }
}

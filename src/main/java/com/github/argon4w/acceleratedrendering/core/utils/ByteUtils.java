package com.github.argon4w.acceleratedrendering.core.utils;

import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class ByteUtils {

    private static final Matrix4f MATRIX = new Matrix4f();

    public static ByteBuffer toBuffer(long address, long size) {
        return MemoryUtil.memByteBufferSafe(address, (int) size);
    }

    public static void putNormal(long address, float value) {
        MemoryUtil.memPutByte(address, (byte) ((int) (Mth.clamp(value, -1.0F, 1.0F) * 127.0F) & 0xFF));
    }

    public static void putMatrix3x4f(long address, Matrix3f matrix) {
        MATRIX.set(matrix).get3x4(MemoryUtil.memByteBuffer(address, 4 * 4 * 3));
    }

    public static void putMatrix4f(long address, Matrix4f matrix) {
        matrix.get(MemoryUtil.memByteBuffer(address, 4 * 4 * 4));
    }

    public static void putByteBuffer(
            ByteBuffer buffer,
            long address,
            long length
    ) {
        MemoryUtil.memCopy(
                MemoryUtil.memAddress0(buffer),
                address,
                length
        );
    }
}

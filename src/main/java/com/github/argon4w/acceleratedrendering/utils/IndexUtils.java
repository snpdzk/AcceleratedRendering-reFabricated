package com.github.argon4w.acceleratedrendering.utils;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class IndexUtils {

    public static ByteBufferBuilder.Result buildIndexBuffer(int count, ByteBufferBuilder.Result indexBuffer, ByteBufferBuilder indexBufferBuilder) {
        if (indexBuffer == null) {
            return null;
        }

        long srcAddress = MemoryUtil.memAddress0(indexBuffer.byteBuffer());
        long destAddress = indexBufferBuilder.reserve((count / 4) * 6 * 4);
        ByteBuffer buffer = MemoryUtil.memByteBuffer(destAddress, (count / 4) * 6 * 4);

        for (int i = 0; i < (count / 4); i++) {
            int srcPosition = i * 4 * 4;

            buffer.putInt(MemoryUtil.memGetInt(srcAddress + srcPosition + 0 * 4));
            buffer.putInt(MemoryUtil.memGetInt(srcAddress + srcPosition + 1 * 4));
            buffer.putInt(MemoryUtil.memGetInt(srcAddress + srcPosition + 2 * 4));
            buffer.putInt(MemoryUtil.memGetInt(srcAddress + srcPosition + 2 * 4));
            buffer.putInt(MemoryUtil.memGetInt(srcAddress + srcPosition + 3 * 4));
            buffer.putInt(MemoryUtil.memGetInt(srcAddress + srcPosition + 0 * 4));
        }

        buffer.flip();
        indexBuffer.close();
        return indexBufferBuilder.build();
    }
}

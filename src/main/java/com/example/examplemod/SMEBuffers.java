package com.example.examplemod;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

public class SMEBuffers {
    public static final ByteBufferBuilder TRANSFORM_INDEX_BUFFER = new ByteBufferBuilder(2 * 32768 * 4);
    public static final ByteBufferBuilder TRANSFORM_BUFFER = new ByteBufferBuilder(2 * 32768 * 4 * 4 * 4);
    public static final ByteBufferBuilder MODEL_PART_BUFFER = new ByteBufferBuilder(36 * 32768);
}

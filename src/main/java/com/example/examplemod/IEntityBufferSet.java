package com.example.examplemod;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

public interface IEntityBufferSet {
    ByteBufferBuilder transformIndexBuffer();
    ByteBufferBuilder transformBuffer();
    ByteBufferBuilder normalBuffer();
}

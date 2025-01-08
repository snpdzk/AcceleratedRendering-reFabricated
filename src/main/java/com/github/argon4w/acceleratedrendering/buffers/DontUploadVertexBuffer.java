package com.github.argon4w.acceleratedrendering.buffers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

public class DontUploadVertexBuffer extends VertexBuffer {

    public DontUploadVertexBuffer() {
        super(Usage.DYNAMIC);
    }

    @Override
    public VertexFormat uploadVertexBuffer(MeshData.DrawState pDrawState, @Nullable ByteBuffer pBuffer) {
        glBindBuffer(GL_ARRAY_BUFFER, BatchedEntityBufferSource.INSTANCE.getVertexBuffer().getBufferHandle());
        DefaultVertexFormat.NEW_ENTITY.setupBufferState();
        return null;
    }
}

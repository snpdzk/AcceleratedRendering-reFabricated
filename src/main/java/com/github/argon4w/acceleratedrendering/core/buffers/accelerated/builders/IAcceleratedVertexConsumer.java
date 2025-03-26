package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IBufferDecorator;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.IBufferGraph;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

public interface IAcceleratedVertexConsumer extends IBufferDecorator {

    void beginTransform(Matrix4f transform, Matrix3f normal);
    void endTransform();
    boolean isAccelerated();
    IBufferGraph getBufferGraph();
    RenderType getRenderType();
    <T> void doRender(IAcceleratedRenderer<T> renderer, T context, Matrix4f transform, Matrix3f normal, int light, int overlay, int color);
    void addClientMesh(ByteBuffer meshBuffer, int size, int color, int light, int overlay);
    void addServerMesh(int offset, int size, int color, int light, int overlay);
}

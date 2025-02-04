package com.github.argon4w.acceleratedrendering.core.buffers.environments;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;

public interface IBufferEnvironment {

    void setupBufferState();
    void uploadSharings(long address);
    void uploadVertex(long address);
    boolean isAccelerated(VertexFormat vertexFormat);
    IServerBuffer getServerMeshBuffer();
    ComputeProgram selectTransformProgram();
    IProgramDispatcher selectCullProgramDispatcher(RenderType renderType);
    IProgramDispatcher selectProcessingProgramDispatcher(VertexFormat.Mode mode);
    VertexFormat getVertexFormat(RenderType renderType);
    int getOffset(VertexFormatElement element);
    int getSharingFlags();
    int getVertexSize();

    final class Presets {

        private static final IBufferEnvironment ENTITY = new VanillaBufferEnvironment(DefaultVertexFormat.NEW_ENTITY);
        private static final IBufferEnvironment POS_TEX_COLOR = new VanillaBufferEnvironment(DefaultVertexFormat.POSITION_TEX_COLOR);
        private static final IBufferEnvironment POS_TEX = new VanillaBufferEnvironment(DefaultVertexFormat.POSITION_TEX);

        public static IBufferEnvironment getEntityEnvironment() {
            return ENTITY;
        }

        public static IBufferEnvironment getPosTexColorEnvironment() {
            return POS_TEX_COLOR;
        }

        public static IBufferEnvironment getPosTexEnvironment() {
            return POS_TEX;
        }

        private Presets() {

        }
    }
}

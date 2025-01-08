package com.github.argon4w.acceleratedrendering.mixins;

import com.github.argon4w.acceleratedrendering.buffers.IVertexConsumerExtension;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements IVertexConsumerExtension {

    @Shadow @Nullable public abstract MeshData build();

    @Override
    public MeshData sme$build() {
        return build();
    }

    @Unique
    @Override
    public void sme$beginTransform(PoseStack.Pose pose) {

    }

    @Override
    public void sme$addMesh(ByteBuffer vertexBuffer, int count, int color, int light, int overlay) {

    }

    @Override
    public boolean sme$supportAcceleratedRendering() {
        return false;
    }

    @Override
    public RenderType sme$getRenderType() {
        return null;
    }
}

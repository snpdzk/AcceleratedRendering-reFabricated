package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris.shadow;

import com.github.argon4w.acceleratedrendering.compat.iris.shadows.IWorldRenderingPipelineExtension;
import com.github.argon4w.acceleratedrendering.compat.iris.shadows.ThrowingShadowBufferSource;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldRenderingPipeline.class)
public interface WorldRenderingPipelineMixin extends IWorldRenderingPipelineExtension {

    @Unique
    @Override
    default MultiBufferSource.BufferSource getShadowBufferSource() {
        return ThrowingShadowBufferSource.INSTANCE;
    }
}

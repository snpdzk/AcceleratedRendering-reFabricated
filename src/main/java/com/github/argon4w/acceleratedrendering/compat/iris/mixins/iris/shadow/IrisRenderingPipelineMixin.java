package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris.shadow;

import com.github.argon4w.acceleratedrendering.compat.iris.shadows.IWorldRenderingPipelineExtension;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(IrisRenderingPipeline.class)
public class IrisRenderingPipelineMixin implements IWorldRenderingPipelineExtension {

    @Shadow @Final private @Nullable ShadowRenderer shadowRenderer;

    @Unique
    @Override
    public MultiBufferSource.BufferSource getShadowBufferSource() {
        return ((ShadowRendererAccessor) shadowRenderer).getBuffers().bufferSource();
    }
}

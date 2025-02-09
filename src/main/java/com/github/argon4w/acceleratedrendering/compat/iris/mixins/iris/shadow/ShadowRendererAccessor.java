package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris.shadow;

import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShadowRenderer.class)
public interface ShadowRendererAccessor {

    @Accessor("buffers")
    RenderBuffers getBuffers();
}

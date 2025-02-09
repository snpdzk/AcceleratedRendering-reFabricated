package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris.buffers;

import com.github.argon4w.acceleratedrendering.compat.iris.buffers.IRenderTypeExtension;
import net.irisshaders.batchedentityrendering.impl.wrappers.TaggingRenderTypeWrapper;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TaggingRenderTypeWrapper.class)
public abstract class TaggingRenderTypeWrapperMixin implements IRenderTypeExtension {

    @Shadow public abstract RenderType unwrap();

    @Unique
    @Override
    public RenderType getOrUnwrap() {
        return unwrap();
    }

    @Unique
    @Override
    public boolean isFastUnwrapSupported() {
        return true;
    }
}

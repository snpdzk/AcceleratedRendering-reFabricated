package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris.buffers;

import com.github.argon4w.acceleratedrendering.compat.iris.buffers.IRenderTypeExtension;
import net.irisshaders.iris.layer.InnerWrappedRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(InnerWrappedRenderType.class)
public abstract class InnerWrappedRenderTypeMixin implements IRenderTypeExtension {

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

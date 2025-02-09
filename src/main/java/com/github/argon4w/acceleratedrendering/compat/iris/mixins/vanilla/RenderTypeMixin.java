package com.github.argon4w.acceleratedrendering.compat.iris.mixins.vanilla;

import com.github.argon4w.acceleratedrendering.compat.iris.buffers.IRenderTypeExtension;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RenderType.class)
public class RenderTypeMixin implements IRenderTypeExtension {

    @Unique
    @Override
    public RenderType getOrUnwrap() {
        return (RenderType) (Object) this;
    }

    @Unique
    @Override
    public boolean isFastUnwrapSupported() {
        return false;
    }
}

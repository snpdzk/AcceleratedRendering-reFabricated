package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris;

import com.github.argon4w.acceleratedrendering.compat.iris.IAcceleratedUnwrap;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WrappableRenderType.class)
public interface WrappableRenderTypeMixin extends IAcceleratedUnwrap {

    @Shadow RenderType unwrap();

    @Unique
    @Override
    default RenderType unwrapFast() {
        return unwrap();
    }

    @Unique
    @Override
    default boolean isAccelerated() {
        return true;
    }
}

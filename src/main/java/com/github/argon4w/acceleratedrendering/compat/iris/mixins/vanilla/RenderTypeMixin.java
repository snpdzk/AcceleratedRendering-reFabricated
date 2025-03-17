package com.github.argon4w.acceleratedrendering.compat.iris.mixins.vanilla;

import com.github.argon4w.acceleratedrendering.compat.iris.IAcceleratedUnwrap;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderType.class)
public class RenderTypeMixin implements IAcceleratedUnwrap {
}

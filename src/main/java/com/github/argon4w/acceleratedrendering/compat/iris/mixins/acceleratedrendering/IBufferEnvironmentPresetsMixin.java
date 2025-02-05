package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IBufferEnvironment.Presets.class)
public class IBufferEnvironmentPresetsMixin {

    @ModifyReturnValue(method = "getEntityEnvironment", at = @At("RETURN"))
    private static IBufferEnvironment useIrisEntityEnvironment(IBufferEnvironment value) {
        return new IrisBufferEnvironment(
                value,
                DefaultVertexFormat.NEW_ENTITY,
                IrisVertexFormats.ENTITY
        );
    }
}

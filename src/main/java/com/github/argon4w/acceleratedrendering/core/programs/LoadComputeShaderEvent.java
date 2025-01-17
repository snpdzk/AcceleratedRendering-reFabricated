package com.github.argon4w.acceleratedrendering.core.programs;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Objects;

public class LoadComputeShaderEvent extends Event implements IModBusEvent {

    private final ImmutableMap.Builder<ResourceLocation, ResourceLocation> shaderLocations;

    public LoadComputeShaderEvent(ImmutableMap.Builder<ResourceLocation, ResourceLocation> builder) {
        this.shaderLocations = builder;
    }

    public void loadComputeShader(ResourceLocation key, ResourceLocation location) {
        shaderLocations.put(key, Objects.requireNonNull(location));
    }
}

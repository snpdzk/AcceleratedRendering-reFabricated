package com.github.argon4w.acceleratedrendering.configs;

import com.github.argon4w.acceleratedrendering.core.meshes.MeshType;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class AcceleratedRenderingConfig {
    public static final AcceleratedRenderingConfig CONFIG;
    public static final ModConfigSpec SPEC;

    public final ModConfigSpec.ConfigValue<FeatureStatus> acceleratedEntityRenderingFeatureStatus;
    public final ModConfigSpec.ConfigValue<DefaultPipeline> acceleratedEntityRenderingDefaultPipeline;
    public final ModConfigSpec.ConfigValue<MeshType> acceleratedEntityRenderingMeshType;

    static {
        Pair<AcceleratedRenderingConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(AcceleratedRenderingConfig::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    private AcceleratedRenderingConfig(ModConfigSpec.Builder builder) {
        builder
                .comment(
                        "Accelerated Entity Rendering Settings.",
                        "Config values that affects the behaviour of accelerated entity rendering."
                )
                .translation("configuration.acceleratedrendering.entities")
                .push("accelerated_entity_rendering");

        acceleratedEntityRenderingFeatureStatus = builder
                .comment("Config value that enables/disables accelerated entity rendering globally.")
                .comment("If disabled, accelerated entity rendering will remain unavailable even if other mods forcibly enable it.")
                .comment("If enabled, accelerated entity rendering will be available and controllable by mods and default_rendering_pipeline config value.")
                .translation("configuration.acceleratedrendering.entities.feature_status")
                .defineEnum("feature_status", FeatureStatus.ENABLED);

        acceleratedEntityRenderingDefaultPipeline = builder
                .comment("Config value that controls default rendering pipeline of accelerated entity rendering.")
                .comment("If it's VANILLA, entities will be not rendered into the accelerated pipeline unless mods explicitly enable it temporarily when rendering their own entities.")
                .comment("If it's ACCELERATED, all entities will be rendered in the accelerated pipeline unless mods explicitly disable it temporarily when rendering their own entities")
                .translation("configuration.acceleratedrendering.entities.default_pipeline")
                .defineEnum("default_pipeline", DefaultPipeline.ACCELERATED);

        acceleratedEntityRenderingMeshType = builder
                .gameRestart()
                .comment("Config value that determines where the cached entity mesh will be stored in.")
                .comment("If it's CLIENT, cached mesh will be stored on the client side (CPU), which will use less VRAM but take more time to upload to the server side (GPU) during rendering.")
                .comment("If it's SERVER, cached mesh will be stored on the server side (GPU), which may speed up rendering but will use more VRAM to store the mesh.")
                .translation("configuration.acceleratedrendering.entities.mesh_type")
                .defineEnum("mesh_type", MeshType.SERVER);

        builder.pop();
    }
}

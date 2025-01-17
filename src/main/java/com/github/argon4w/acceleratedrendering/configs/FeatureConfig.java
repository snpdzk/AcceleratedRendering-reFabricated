package com.github.argon4w.acceleratedrendering.configs;

import com.github.argon4w.acceleratedrendering.core.meshes.MeshType;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FeatureConfig {
    public static final FeatureConfig CONFIG;
    public static final ModConfigSpec SPEC;

    public final ModConfigSpec.IntValue cpuRenderAheadLimit;

    public final ModConfigSpec.ConfigValue<FeatureStatus> acceleratedEntityRenderingFeatureStatus;
    public final ModConfigSpec.ConfigValue<DefaultPipeline> acceleratedEntityRenderingDefaultPipeline;
    public final ModConfigSpec.ConfigValue<MeshType> acceleratedEntityRenderingMeshType;

    public final ModConfigSpec.ConfigValue<FeatureStatus> normalCullingFeatureStatus;
    public final ModConfigSpec.ConfigValue<FeatureStatus> normalCullingIgnoreCullState;

    static {
        Pair<FeatureConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(FeatureConfig::new);

        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    private FeatureConfig(ModConfigSpec.Builder builder) {
        cpuRenderAheadLimit = builder
                .comment("Frames that CPU is allowed to be ahead of GPU")
                .comment("It's not recommended to change this value because it may cause visual glitches during rendering.")
                .translation("acceleratedrendering.configuration.render_ahead_limit")
                .defineInRange("render_ahead_limit", 0, 0, Integer.MAX_VALUE);

        builder
                .comment("Accelerated Entity Rendering Settings")
                .comment("Accelerated Entity Rendering uses GPU to cache and transform vertices whiling rendering model parts of entities, instead of generating and transforming vertices every time the model parts are rendered in CPU.")
                .translation("acceleratedrendering.configuration.accelerated_entity_rendering")
                .push("accelerated_entity_rendering");

        acceleratedEntityRenderingFeatureStatus = builder
                .comment("- DISABLED: Disable accelerated entity rendering.")
                .comment("- ENABLED: Enable accelerated entity rendering.")
                .translation("acceleratedrendering.configuration.accelerated_entity_rendering.feature_status")
                .defineEnum("feature_status", FeatureStatus.ENABLED);

        acceleratedEntityRenderingDefaultPipeline = builder
                .comment("- VANILLA: entities will be not rendered into the accelerated pipeline unless mods explicitly enable it temporarily when rendering their own entities.")
                .comment("- ACCELERATED: all entities will be rendered in the accelerated pipeline unless mods explicitly disable it temporarily when rendering their own entities.")
                .translation("acceleratedrendering.configuration.accelerated_entity_rendering.default_pipeline")
                .defineEnum("default_pipeline", DefaultPipeline.ACCELERATED);

        acceleratedEntityRenderingMeshType = builder
                .gameRestart()
                .comment("- CLIENT: cached mesh will be stored on the client side (CPU), which will use less VRAM but take more time to upload to the server side (GPU) during rendering.")
                .comment("- SERVER: cached mesh will be stored on the server side (GPU), which may speed up rendering but will use more VRAM to store the mesh.")
                .translation("acceleratedrendering.configuration.accelerated_entity_rendering.mesh_type")
                .defineEnum("mesh_type", MeshType.SERVER);

        builder.pop();

        builder
                .comment("Simple Normal Face Culling Settings")
                .comment("Simple Normal face culling uses an compute shader before the draw call to discard faces that is not visible on screen by checking if the normal is facing to the screen.")
                .translation("acceleratedrendering.configuration.normal_culling")
                .push("normal_culling");

        normalCullingFeatureStatus = builder
                .comment("- DISABLED: Disable simple normal face culling.")
                .comment("- ENABLED: Enable simple normal face culling.")
                .translation("acceleratedrendering.configuration.normal_culling.feature_status")
                .defineEnum("feature_Status", FeatureStatus.ENABLED);

        normalCullingIgnoreCullState = builder
                .comment("- DISABLED: Simple normal face culling will not cull entities that are not declared as \"cullable\".")
                .comment("- ENABLED: Simple normal face culling will cull all entities even if they are not declared as \"cullable\".")
                .translation("acceleratedrendering.configuration.normal_culling.ignore_cull_state")
                .defineEnum("ignore_cull_state", FeatureStatus.DISABLED);
    }
}

package com.github.argon4w.acceleratedrendering.configs;

import com.github.argon4w.acceleratedrendering.core.meshes.MeshType;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FeatureConfig {
    public static final FeatureConfig CONFIG;
    public static final ModConfigSpec SPEC;

    public final ModConfigSpec.IntValue corePooledBufferSetSize;
    public final ModConfigSpec.IntValue corePooledElementBufferSize;
    public final ModConfigSpec.ConfigValue<FeatureStatus> coreUseVanillaBatching;

    public final ModConfigSpec.ConfigValue<FeatureStatus> acceleratedEntityRenderingFeatureStatus;
    public final ModConfigSpec.ConfigValue<PipelineSetting> acceleratedEntityRenderingDefaultPipeline;
    public final ModConfigSpec.ConfigValue<MeshType> acceleratedEntityRenderingMeshType;

    public final ModConfigSpec.ConfigValue<FeatureStatus> normalCullingFeatureStatus;
    public final ModConfigSpec.ConfigValue<FeatureStatus> normalCullingDefaultCulling;
    public final ModConfigSpec.ConfigValue<FeatureStatus> normalCullingIgnoreCullState;

    public final ModConfigSpec.ConfigValue<FeatureStatus> irisCompatFeatureStatus;
    public final ModConfigSpec.ConfigValue<FeatureStatus> irisCompatNormalCullingCompat;
    public final ModConfigSpec.ConfigValue<FeatureStatus> irisCompatShadowCulling;
    public final ModConfigSpec.ConfigValue<FeatureStatus> irisCompatEntitiesCompat;
    public final ModConfigSpec.ConfigValue<FeatureStatus> irisCompatPolygonProcessing;

    static {
        Pair<FeatureConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(FeatureConfig::new);

        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    private FeatureConfig(ModConfigSpec.Builder builder) {
        builder
                .comment("Core Settings")
                .comment("Core Settings allows you to change setting that are related to all rendering features.")
                .translation("acceleratedrendering.configuration.core_settings")
                .push("core_settings");

        corePooledBufferSetSize = builder
                .gameRestart()
                .comment("Count of buffer sets that holds data for in-flight frame rendering.")
                .comment("Changing this value may affects your FPS. Smaller value means less in-flight frames, while larger values means more in-flight frames. More in-flight frames means more FPS but more VRAM.")
                .translation("acceleratedrendering.configuration.core_settings.pooled_buffer_set_size")
                .defineInRange("pooled_buffer_set_size", 5, 0, Integer.MAX_VALUE);

        corePooledElementBufferSize = builder
                .gameRestart()
                .comment("Count of batches of RenderTypes that is allowed in a draw call.")
                .comment("Changing this value may affects your FPS. Smaller value means less batches allowed in a draw call, while larger values means more batches. More batches means more FPS but more VRAM and more CPU pressure on handling RenderTypes.")
                .translation("acceleratedrendering.configuration.core_settings.pooled_element_buffer_size")
                .defineInRange("pooled_element_buffer_size", 32, 0, Integer.MAX_VALUE);

        coreUseVanillaBatching = builder
                .comment("- DISABLED: Unsupported RenderType will fallback to vanilla rendering pipeline.")
                .comment("- ENABLED: Unsupported RenderType will usea \"vanilla-like\" rendering pipeline that batches draw calls and improves performance.")
                .translation("acceleratedrendering.configuration.core_settings.use_vanilla_batching")
                .defineEnum("use_vanilla_batching", FeatureStatus.ENABLED);

        builder.pop();

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
                .comment("- VANILLA: entities will not be rendered into the accelerated pipeline unless mods explicitly enable it temporarily when rendering their own entities.")
                .comment("- ACCELERATED: all entities will be rendered in the accelerated pipeline unless mods explicitly disable it temporarily when rendering their own entities.")
                .translation("acceleratedrendering.configuration.accelerated_entity_rendering.default_pipeline")
                .defineEnum("default_pipeline", PipelineSetting.ACCELERATED);

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

        normalCullingDefaultCulling = builder
                .comment("- DISABLED: Faces will not be culled unless mods explicitly enable it temporarily when rendering their own faces.")
                .comment("- ENABLED: all faces will be culled unless mods explicitly disable it temporarily when rendering their own faces.")
                .translation("acceleratedrendering.configuration.normal_culling.default_culling")
                .defineEnum("default_culling", FeatureStatus.ENABLED);

        normalCullingIgnoreCullState = builder
                .comment("- DISABLED: Simple normal face culling will not cull entities that are not declared as \"cullable\".")
                .comment("- ENABLED: Simple normal face culling will cull all entities even if they are not declared as \"cullable\".")
                .translation("acceleratedrendering.configuration.normal_culling.ignore_cull_state")
                .defineEnum("ignore_cull_state", FeatureStatus.DISABLED);

        builder.pop();

        builder
                .comment("Iris Compatibility Settings")
                .comment("Iris Compatibility Settings allows Accelerated Rendering to work correctly with Iris.")
                .translation("acceleratedrendering.configuration.iris_compatibility")
                .push("iris_compatibility");

        irisCompatFeatureStatus = builder
                .comment("- DISABLED: Accelerated Rendering will be incompatible with Iris and cause visual glitches when working with Iris.")
                .comment("- ENABLED: Accelerated Rendering will use compute shaders that fits Iris's vertex formats, which make it compatible with Iris.")
                .translation("acceleratedrendering.configuration.iris_compatibility.feature_status")
                .defineEnum("feature_status", FeatureStatus.ENABLED);

        irisCompatNormalCullingCompat = builder
                .comment("- DISABLED: Simple Normal culling will not work with Iris because the culling shader is for vanilla's vertex formats.")
                .comment("- ENABLED: Normal culling will use another culling shader that fits iris's vertex format, which make it compatible with Iris.")
                .translation("acceleratedrendering.configuration.iris_compatibility.normal_culling_compatibility")
                .defineEnum("normal_culling_compatibility", FeatureStatus.ENABLED);

        irisCompatShadowCulling = builder
                .comment("- DISABLED: Entities will not be culled when they are rendered as shadows. Which reduce FPS due to redundant faces.")
                .comment("- ENABLED: Entities will be culled when they are rendered as shadows. Redundant faces will be culled and improve FPS, but it may cause incorrect shadows.")
                .translation("acceleratedrendering.configuration.iris_compatibility.shadow_culling")
                .defineEnum("shadow_culling", FeatureStatus.ENABLED);

        irisCompatEntitiesCompat = builder
                .comment("- DISABLED: renderEntity called from Iris will not render entity into the accelerated pipeline.")
                .comment("- ENABLED: renderEntity called from Iris will render entity into the accelerated pipeline.")
                .translation("acceleratedrendering.configuration.iris_compatibility.entities_compatibility")
                .defineEnum("entities_compatibility", FeatureStatus.ENABLED);

        irisCompatPolygonProcessing = builder
                .comment("- DISABLED: Extra information in vertices provided by Iris will not be included or calculated in the accelerated pipeline, which may cause visual glitches or incorrect rendering.")
                .comment("- DISABLED: Extra information in vertices provided by Iris will be included and calculated in the accelerated pipeline by a compute shader.")
                .translation("acceleratedrendering.configuration.iris_compatibility.polygon_processing")
                .defineEnum("polygon_processing", FeatureStatus.ENABLED);

        builder.pop();
    }
}

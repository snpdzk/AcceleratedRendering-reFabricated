package com.github.argon4w.acceleratedrendering.compat.iris.programs;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.core.programs.LoadComputeShaderEvent;
import com.github.argon4w.acceleratedrendering.core.programs.culling.LoadCullingProgramSelectorEvent;
import com.github.argon4w.acceleratedrendering.core.programs.transform.LoadTransformProgramSelectorEvent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;

public class IrisPrograms {

    public static final ResourceLocation IRIS_ENTITY_VERTEX_TRANSFORM_KEY = AcceleratedRenderingModEntry.location("compat_entity_vertex_transform_iris");
    public static final ResourceLocation IRIS_ENTITY_POLYGON_CULLING_KEY = AcceleratedRenderingModEntry.location("compat_entity_polygon_cull_iris");

    @SubscribeEvent
    public static void onLoadComputeShaders(LoadComputeShaderEvent event) {
        event.loadComputeShader(IRIS_ENTITY_VERTEX_TRANSFORM_KEY, AcceleratedRenderingModEntry.location("shaders/compat/transform/iris_entity_vertex_transform_shader.compute"));
        event.loadComputeShader(IRIS_ENTITY_POLYGON_CULLING_KEY, AcceleratedRenderingModEntry.location("shaders/compat/culling/iris_entity_polygon_culling_shader.compute"));
    }

    @SubscribeEvent
    public static void onLoadTransformPrograms(LoadTransformProgramSelectorEvent event) {
        event.loadFor(DefaultVertexFormat.NEW_ENTITY, parent -> new IrisTransformProgramSelector(
                parent,
                IrisVertexFormats.ENTITY,
                IRIS_ENTITY_VERTEX_TRANSFORM_KEY
        ));
    }

    @SubscribeEvent
    public static void onLoadCullingPrograms(LoadCullingProgramSelectorEvent event) {
        event.loadFor(DefaultVertexFormat.NEW_ENTITY, parent -> new IrisCullingProgramSelector(
                parent,
                IrisVertexFormats.ENTITY,
                IRIS_ENTITY_POLYGON_CULLING_KEY
        ));
    }
}

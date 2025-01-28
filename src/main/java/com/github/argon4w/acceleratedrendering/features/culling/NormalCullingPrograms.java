package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.core.programs.LoadComputeShaderEvent;
import com.github.argon4w.acceleratedrendering.core.programs.culling.LoadCullingProgramSelectorEvent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = AcceleratedRenderingModEntry.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NormalCullingPrograms {

    public static final ResourceLocation CORE_POS_TEX_COLOR_POLYGON_CULLING_KEY = AcceleratedRenderingModEntry.location("core_pos_tex_polygon_culling");
    public static final ResourceLocation CORE_ENTITY_POLYGON_CULLING_KEY = AcceleratedRenderingModEntry.location("core_entity_polygon_culling");

    @SubscribeEvent
    public static void onLoadComputeShaders(LoadComputeShaderEvent event) {
        event.loadComputeShader(CORE_ENTITY_POLYGON_CULLING_KEY, AcceleratedRenderingModEntry.location("shaders/core/culling/entity_polygon_culling_shader.compute"));
        event.loadComputeShader(CORE_POS_TEX_COLOR_POLYGON_CULLING_KEY, AcceleratedRenderingModEntry.location("shaders/core/culling/pos_tex_color_polygon_culling_shader.compute"));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLoadCullingPrograms(LoadCullingProgramSelectorEvent event) {
        event.loadFor(DefaultVertexFormat.NEW_ENTITY, parent -> new NormalCullingProgramSelector(
                parent,
                DefaultVertexFormat.NEW_ENTITY,
                CORE_ENTITY_POLYGON_CULLING_KEY
        ));

        event.loadFor(DefaultVertexFormat.POSITION_TEX_COLOR, parent -> new NormalCullingProgramSelector(
                parent,
                DefaultVertexFormat.POSITION_TEX_COLOR,
                CORE_POS_TEX_COLOR_POLYGON_CULLING_KEY
        ));
    }
}

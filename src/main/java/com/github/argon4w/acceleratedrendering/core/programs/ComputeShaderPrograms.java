package com.github.argon4w.acceleratedrendering.core.programs;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.core.backends.programs.BarrierFlags;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;

public class ComputeShaderPrograms {

    public static final ResourceLocation CORE_BLOCK_VERTEX_TRANSFORM_KEY = AcceleratedRenderingModEntry.location("core_block_vertex_transform");
    public static final ResourceLocation CORE_ENTITY_VERTEX_TRANSFORM_KEY = AcceleratedRenderingModEntry.location("core_entity_vertex_transform");
    public static final ResourceLocation CORE_POS_TEX_COLOR_VERTEX_TRANSFORM_KEY = AcceleratedRenderingModEntry.location("core_pos_tex_color_vertex_transform");
    public static final ResourceLocation CORE_POS_TEX_VERTEX_TRANSFORM_KEY = AcceleratedRenderingModEntry.location("core_pos_tex_vertex_transform");
    public static final ResourceLocation CORE_POS_COLOR_TEX_LIGHT_VERTEX_TRANSFORM_KEY = AcceleratedRenderingModEntry.location("core_pos_color_tex_light_vertex_transform");
    public static final ResourceLocation CORE_PASS_THROUGH_QUAD_CULLING_KEY = AcceleratedRenderingModEntry.location("core_pass_through_quad_culling");
    public static final ResourceLocation CORE_PASS_THROUGH_TRIANGLE_CULLING_KEY = AcceleratedRenderingModEntry.location("core_pass_through_triangle_culling");

    @SubscribeEvent
    public static void onLoadComputeShaders(LoadComputeShaderEvent event) {
        event.loadComputeShader(
                CORE_BLOCK_VERTEX_TRANSFORM_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/transform/block_vertex_transform_shader.compute"),
                BarrierFlags.SHADER_STORAGE
        );

        event.loadComputeShader(
                CORE_ENTITY_VERTEX_TRANSFORM_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/transform/entity_vertex_transform_shader.compute"),
                BarrierFlags.SHADER_STORAGE
        );

        event.loadComputeShader(
                CORE_POS_TEX_COLOR_VERTEX_TRANSFORM_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/transform/pos_tex_color_vertex_transform_shader.compute"),
                BarrierFlags.SHADER_STORAGE
        );

        event.loadComputeShader(
                CORE_POS_TEX_VERTEX_TRANSFORM_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/transform/pos_tex_vertex_transform_shader.compute"),
                BarrierFlags.SHADER_STORAGE
        );

        event.loadComputeShader(
                CORE_POS_COLOR_TEX_LIGHT_VERTEX_TRANSFORM_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/transform/pos_color_tex_light_vertex_transform_shader.compute"),
                BarrierFlags.SHADER_STORAGE
        );

        event.loadComputeShader(
                CORE_PASS_THROUGH_QUAD_CULLING_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/culling/pass_through_quad_culling_shader.compute"),
                BarrierFlags.SHADER_STORAGE,
                BarrierFlags.ATOMIC_COUNTER
        );

        event.loadComputeShader(
                CORE_PASS_THROUGH_TRIANGLE_CULLING_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/culling/pass_through_triangle_culling_shader.compute"),
                BarrierFlags.SHADER_STORAGE,
                BarrierFlags.ATOMIC_COUNTER
        );
    }
}

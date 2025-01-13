package com.github.argon4w.acceleratedrendering.core.programs;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL46.*;

@EventBusSubscriber(modid = AcceleratedRenderingModEntry.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ComputeShaderPrograms {

    public static final ResourceLocation CORE_ENTITY_COMPUTE_SHADER_KEY = AcceleratedRenderingModEntry.location("core_entity_compute_shader");
    public static final ResourceLocation CORE_ENTITY_POLYGON_CULL_KEY = AcceleratedRenderingModEntry.location("core_entity_polygon_cull_compute_shader");
    public static final ResourceLocation CORE_POS_TEX_COLOR_COMPUTE_SHADER_KEY = AcceleratedRenderingModEntry.location("core_pos_tex_color_compute_shader");
    public static final ResourceLocation CORE_POS_TEX_COLOR_POLYGON_CULL_KEY = AcceleratedRenderingModEntry.location("core_pos_tex_polygon_cull_compute_shader");

    private static final Map<ResourceLocation, ResourceLocation> COMPUTE_SHADER_LOCATIONS = initComputeShaders(new Object2ObjectOpenHashMap<>());
    private static final Map<ResourceLocation, Program> COMPUTE_SHADER_HANDLES = new Object2ObjectOpenHashMap<>();

    public static Map<ResourceLocation, ResourceLocation> initComputeShaders(Map<ResourceLocation, ResourceLocation> map) {
        map.put(CORE_ENTITY_COMPUTE_SHADER_KEY, AcceleratedRenderingModEntry.location("shaders/core/entity_vertex_transform_shader.compute"));
        map.put(CORE_ENTITY_POLYGON_CULL_KEY, AcceleratedRenderingModEntry.location("shaders/core/entity_polygon_cull_shader.compute"));
        map.put(CORE_POS_TEX_COLOR_COMPUTE_SHADER_KEY, AcceleratedRenderingModEntry.location("shaders/core/pos_tex_color_vertex_transform_shader.compute"));
        map.put(CORE_POS_TEX_COLOR_POLYGON_CULL_KEY, AcceleratedRenderingModEntry.location("shaders/core/pos_tex_color_polygon_cull_shader.compute"));

        return map;
    }

    @SubscribeEvent
    public static void onRegisterResourceReloadListenerEvent(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SimplePreparableReloadListener<Map<ResourceLocation, String>>() {
            @Override
            protected Map<ResourceLocation, String> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                try {
                    Map<ResourceLocation, String> shaderSources = new Object2ObjectOpenHashMap<>();

                    for (ResourceLocation key : COMPUTE_SHADER_LOCATIONS.keySet()) {
                        ResourceLocation resourceLocation = COMPUTE_SHADER_LOCATIONS.get(key);

                        Optional<Resource> resource = resourceManager.getResource(resourceLocation);

                        if (resource.isEmpty()) {
                            throw new IllegalStateException("Cannot found compute shader: \"" + resourceLocation + "\"");
                        }

                        try (InputStream stream = resource.get().open()) {
                            shaderSources.put(key, new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                        }
                    }

                    return shaderSources;
                } catch (Exception e) {
                    throw new ReportedException(CrashReport.forThrowable(e, "Exception while loading compute shader"));
                }
            }

            @Override
            protected void apply(Map<ResourceLocation, String> shaderSources, ResourceManager resourceManager, ProfilerFiller profiler) {
                RenderSystem.recordRenderCall(() -> {
                    for (ResourceLocation key : shaderSources.keySet()) {
                        try {
                            String shaderSource = shaderSources.get(key);
                            Program program = new Program();
                            Shader shader = new Shader(GL_COMPUTE_SHADER);

                            shader.setShaderSource(shaderSource);

                            if (!shader.compileShader()) {
                                throw new IllegalStateException("Shader \"" + key + "\" failed to compile because of the following errors: " + shader.getInfoLog());
                            }

                            program.attachShader(shader);

                            if (!program.linkProgram()) {
                                throw new IllegalStateException("Program \"" + key + "\" failed to link because of the following errors: " + program.getInfoLog());
                            }

                            shader.delete();
                            COMPUTE_SHADER_HANDLES.put(key, program);
                        } catch (Exception e) {
                            throw new ReportedException(CrashReport.forThrowable(e, "Exception while compiling/linking compute shader"));
                        }
                    }
                });
            }
        });
    }

    public static Program getProgram(ResourceLocation resourceLocation) {
        Program program = COMPUTE_SHADER_HANDLES.get(resourceLocation);

        if (program == null) {
            throw new IllegalStateException("Get shader program \""+ resourceLocation + "\" too early! Program is not loaded yet!");
        }

        return program;
    }
}

package com.github.argon4w.acceleratedrendering.core.programs;

import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Shader;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL46.GL_COMPUTE_SHADER;

public class ComputeShaderProgramLoader extends SimplePreparableReloadListener<Map<ResourceLocation, String>> {

    private static final Map<ResourceLocation, Program> COMPUTE_SHADER_HANDLES = new Object2ObjectOpenHashMap<>();
    static final ComputeShaderProgramLoader INSTANCE = new ComputeShaderProgramLoader();

    @Override
    protected Map<ResourceLocation, String> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        try {
            ImmutableMap.Builder<ResourceLocation, ResourceLocation> builder = ImmutableMap.builder();
            ModLoader.postEvent(new LoadComputeShaderEvent(builder));

            Map<ResourceLocation, String> shaderSources = new Object2ObjectOpenHashMap<>();
            Map<ResourceLocation, ResourceLocation> shaderLocations = builder.build();

            for (ResourceLocation key : shaderLocations.keySet()) {
                ResourceLocation resourceLocation = shaderLocations.get(key);

                if (resourceLocation == null) {
                    throw new IllegalStateException("Found empty shader location on: \"" + key + "\"");
                }

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

    public static Program getProgram(ResourceLocation resourceLocation) {
        Program program = COMPUTE_SHADER_HANDLES.get(resourceLocation);

        if (program == null) {
            throw new IllegalStateException("Get shader program \""+ resourceLocation + "\" too early! Program is not loaded yet!");
        }

        return program;
    }
}

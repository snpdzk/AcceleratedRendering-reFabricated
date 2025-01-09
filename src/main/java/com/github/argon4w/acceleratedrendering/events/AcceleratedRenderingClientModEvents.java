package com.github.argon4w.acceleratedrendering.events;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.mojang.blaze3d.systems.RenderSystem;
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

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL46.*;

@EventBusSubscriber(modid = AcceleratedRenderingModEntry.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AcceleratedRenderingClientModEvents {

    private static final ResourceLocation COMPUTE_SHADER_LOCATION = ResourceLocation.fromNamespaceAndPath(AcceleratedRenderingModEntry.MODID, "shaders/core/vertex_transform_shader_neo.compute");
    private static int shaderProgram = -1;

    @SubscribeEvent
    public static void onRegisterResourceReloadListenerEvent(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SimplePreparableReloadListener<String>() {
            @Override
            protected String prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                try {
                    Optional<Resource> resource = pResourceManager.getResource(COMPUTE_SHADER_LOCATION);

                    if (resource.isEmpty()) {
                        throw new IllegalStateException("Cannot found compute shader");
                    }

                    try (BufferedReader reader = resource.get().openAsReader()) {
                        return reader.lines().map(s -> s + '\n').collect(Collectors.joining());
                    }
                } catch (Exception e) {
                    throw new ReportedException(CrashReport.forThrowable(e, "Exception while loading compute shader"));
                }
            }

            @Override
            protected void apply(String pShaderSource, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                try {
                    RenderSystem.recordRenderCall(() -> {
                        int shader = glCreateShader(GL_COMPUTE_SHADER);
                        int program = glCreateProgram();

                        glShaderSource(shader, pShaderSource);
                        glCompileShader(shader);

                        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                            throw new IllegalStateException(glGetShaderInfoLog(shader));
                        }

                        glAttachShader(program, shader);
                        glLinkProgram(program);

                        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
                            throw new IllegalStateException(glGetProgramInfoLog(program));
                        }

                        glDeleteShader(shader);
                        shaderProgram = program;
                    });
                }catch (Exception e) {
                    throw new ReportedException(CrashReport.forThrowable(e, "Exception while compiling/linking compute shader"));
                }
            }
        });
    }

    public static int getShaderProgram() {
        RenderSystem.assertOnRenderThread();

        if (shaderProgram < 0) {
            throw new IllegalStateException("Query shader program too early! Program is not loaded yet!");
        }

        return shaderProgram;
    }
}

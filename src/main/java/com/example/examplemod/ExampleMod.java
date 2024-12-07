package com.example.examplemod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import static org.lwjgl.opengl.GL46.*;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static int program;

    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        RenderSystem.recordRenderCall(() -> {
            int shader = glCreateShader(GL_COMPUTE_SHADER);
            glShaderSource(shader, """
                    #version 460 core
                    
                    struct Vertex {
                        float x;
                        float y;
                        float z;
                        int color;
                        float u0;
                        float v0;
                        int uv1;
                        int uv2;
                        int normal;
                    };
                    
                    layout(local_size_x=4, local_size_y=1) in;
                    layout(binding=0, std430) buffer Data {
                        Vertex vertices[];
                    } data;
                    layout(binding=1, std430) readonly buffer Transforms {
                        mat4 matrices[];
                    } transforms;
                    layout(binding=2, std430) readonly buffer Indices {
                        int indices[];
                    } indices;
                    
                    void main() {
                        int index = int(gl_GlobalInvocationID.x);
                        int transformIndex = indices.indices[index];
                        if (transformIndex < 0) {
                            return;
                        }
                        Vertex vertex = data.vertices[index];
                        vec3 pos = vec3(vertex.x, vertex.y, vertex.z);
                        vec4 transformed = transforms.matrices[transformIndex] * vec4(pos, 1.0);
                        data.vertices[index].x = transformed.x;
                        data.vertices[index].y = transformed.y;
                        data.vertices[index].z = transformed.z;
                    }
                    """);
            glCompileShader(shader);

            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                System.out.println("Shader compile error: " + glGetShaderInfoLog(shader));
                return;
            }

            program = glCreateProgram();
            glAttachShader(program, shader);
            glLinkProgram(program);

            if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
                System.out.println("Program link error: " + glGetProgramInfoLog(program));
            }

            /*glUseProgram(program);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, inputBuffer);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, outputBuffer);

            glDispatchCompute(20, 1, 1);
            glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);

            FloatBuffer resultBuffer = memoryStack.mallocFloat(4 * 4 * 20);*/
        });
    }
}

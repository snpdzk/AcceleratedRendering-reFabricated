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
                        uint normal;
                    };
                    
                    layout(local_size_x=1, local_size_y=1) in;
                    
                    layout(location=0) uniform int offsetY;
                    
                    layout(binding=0, std430) buffer Data {
                        Vertex vertices[];
                    } data;
                    layout(binding=1, std430) readonly buffer Transforms {
                        mat4 matrices[];
                    } transforms;
                    layout(binding=2, std430) readonly buffer Normals {
                        mat3 matrices[];
                    } normals;
                    layout(binding=3, std430) readonly buffer Indices {
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
                        int normalZ = int((vertex.normal >> 16) & 0xFFu);
                        int normalY = int((vertex.normal >> 8) & 0xFFu);
                        int normalX = int((vertex.normal >> 0) & 0xFFu);
                        if (normalX > 127) normalX -= 256;
                        if (normalY > 127) normalY -= 256;
                        if (normalZ > 127) normalZ -= 256;
                        vec3 normal = vec3(normalX / 127.0, normalY / 127.0, normalZ / 127.0);
                        vec4 transformed = transforms.matrices[transformIndex] * vec4(pos, 1.0);
                        vec3 transformedNormal = normals.matrices[transformIndex] * normal;
                        data.vertices[index].x = transformed.x;
                        data.vertices[index].y = transformed.y + offsetY;
                        data.vertices[index].z = transformed.z;
                        uint transformedNormalX = uint(int(clamp(transformedNormal.x, -1.0, 1.0) * 127.0) & 0xFF);
                        uint transformedNormalY = uint(int(clamp(transformedNormal.y, -1.0, 1.0) * 127.0) & 0xFF);
                        uint transformedNormalZ = uint(int(clamp(transformedNormal.z, -1.0, 1.0) * 127.0) & 0xFF);
                        data.vertices[index].normal = (transformedNormalX << 16) | (transformedNormalY << 8) | (transformedNormalZ << 0);
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
        });
    }
}

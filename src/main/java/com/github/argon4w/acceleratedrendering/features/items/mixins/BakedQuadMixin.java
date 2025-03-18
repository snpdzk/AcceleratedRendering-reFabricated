package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshCollector;
import com.github.argon4w.acceleratedrendering.core.utils.CullerUtils;
import com.github.argon4w.acceleratedrendering.core.utils.TextureUtils;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedQuad;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements IAcceleratedBakedQuad {

    @Unique private static final Map<int[], Map<RenderType, IMesh>> MESHES = new Reference2ObjectOpenHashMap<>();

    @Shadow @Final protected int[] vertices;

    @Unique
    @Override
    public void renderFast(IAcceleratedVertexConsumer extension, int combinedLight, int combinedOverlay, int color) {
        Map<RenderType, IMesh> meshes = MESHES.get(vertices);

        if (meshes == null) {
            meshes = new Object2ObjectOpenHashMap<>();
            MESHES.put(vertices, meshes);
        }

        if (hasCustomColor()) {
            color = getCustomColor();
        }

        for (RenderType renderType : extension.getRenderTypes()) {
            IMesh mesh = meshes.get(renderType);

            if (mesh != null) {
                mesh.write(
                        extension,
                        color,
                        combinedLight,
                        combinedOverlay
                );
                continue;
            }

            IMesh.Builder builder = AcceleratedItemRenderingFeature.getMeshBuilder();
            MeshCollector meshCollector = builder.newMeshCollector(renderType);
            NativeImage image = TextureUtils.downloadTexture(renderType, 0);

            int size = vertices.length / 8;

            ModelPart.Vertex[] modelVertices = new ModelPart.Vertex[size];
            int[] modelColors = new int[size];
            Vector3f[] modelNormals = new Vector3f[size];

            for (int i = 0; i < size; i++) {
                int vertexOffset = i * IQuadTransformer.STRIDE;
                int posOffset = vertexOffset + IQuadTransformer.POSITION;
                int colorOffset = vertexOffset + IQuadTransformer.COLOR;
                int uv0Offset = vertexOffset + IQuadTransformer.UV0;
                int normalOffset = vertexOffset + IQuadTransformer.NORMAL;

                float posX = Float.intBitsToFloat(vertices[posOffset + 0]);
                float posY = Float.intBitsToFloat(vertices[posOffset + 1]);
                float posZ = Float.intBitsToFloat(vertices[posOffset + 2]);

                int packedColor = vertices[colorOffset];

                float u0 = Float.intBitsToFloat(vertices[uv0Offset + 0]);
                float v0 = Float.intBitsToFloat(vertices[uv0Offset + 1]);

                int packedNormal = vertices[normalOffset];
                float normalX = ((byte) (packedNormal & 0xFF)) / 127.0f;
                float normalY = ((byte) ((packedNormal >> 8) & 0xFF)) / 127.0f;
                float normalZ = ((byte) ((packedNormal >> 16) & 0xFF)) / 127.0f;

                modelVertices[i] = new ModelPart.Vertex(posX, posY, posZ, u0, v0);
                modelColors[i] = packedColor;
                modelNormals[i] = new Vector3f(normalX, normalY, normalZ);
            }

            if (!CullerUtils.shouldCull(modelVertices, image)) {
                for (int i = 0; i < size; i ++) {
                    ModelPart.Vertex vertex = modelVertices[i];
                    int packedColor = modelColors[i];
                    Vector3f normal = modelNormals[i];

                    meshCollector.addVertex(
                            vertex.pos.x,
                            vertex.pos.y,
                            vertex.pos.z,
                            packedColor,
                            vertex.u,
                            vertex.v,
                            combinedOverlay,
                            combinedLight,
                            normal.x,
                            normal.y,
                            normal.z
                    );
                }
            }

            mesh = builder.build(meshCollector);

            meshes.put(renderType, mesh);
            mesh.write(
                    extension,
                    color,
                    combinedLight,
                    combinedOverlay
            );
        }
    }

    @Unique
    @Override
    public boolean hasCustomColor() {
        return false;
    }

    @Unique
    @Override
    public int getCustomColor() {
        return -1;
    }
}

package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.IBufferGraph;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshCollector;
import com.github.argon4w.acceleratedrendering.core.utils.CullerUtils;
import com.github.argon4w.acceleratedrendering.core.utils.LazyMap;
import com.github.argon4w.acceleratedrendering.core.utils.TextureUtils;
import com.github.argon4w.acceleratedrendering.core.utils.Vertex;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedQuad;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(BakedQuad.class)
public abstract class BakedQuadMixin implements IAcceleratedBakedQuad {

    @Unique private static final Map<int[], Map<IBufferGraph, IMesh>> MESHES = new LazyMap<>(new Reference2ObjectOpenHashMap<>(), Reference2ObjectOpenHashMap::new);

    @Shadow @Final protected int[] vertices;

    @Shadow public abstract boolean isTinted();

    @Unique
    @Override
    public void renderFast(
            Matrix4f transform,
            Matrix3f normal,
            IAcceleratedVertexConsumer extension,
            int combinedLight,
            int combinedOverlay,
            int color
    ) {
        IBufferGraph bufferGraph = extension.getBufferGraph();
        RenderType renderType = bufferGraph.getRenderType();

        Map<IBufferGraph, IMesh> meshes = MESHES.get(vertices);
        IMesh mesh = meshes.get(bufferGraph);

        if (mesh != null) {
            mesh.write(
                    extension,
                    getCustomColor(color),
                    combinedLight,
                    combinedOverlay
            );
            return;
        }

        NativeImage texture = TextureUtils.downloadTexture(renderType, 0);
        MeshCollector meshCollector = new MeshCollector(renderType.format);
        VertexConsumer meshBuilder = extension.decorate(meshCollector);

        int size = vertices.length / 8;
        Vertex[] polygon = new Vertex[size];

        for (int i = 0; i < size; i++) {
            int vertexOffset = i * IQuadTransformer.STRIDE;
            int posOffset = vertexOffset + IQuadTransformer.POSITION;
            int colorOffset = vertexOffset + IQuadTransformer.COLOR;
            int uv0Offset = vertexOffset + IQuadTransformer.UV0;
            int uv2Offset = vertexOffset + IQuadTransformer.UV2;
            int normalOffset = vertexOffset + IQuadTransformer.NORMAL;

            float posX = Float.intBitsToFloat(vertices[posOffset + 0]);
            float posY = Float.intBitsToFloat(vertices[posOffset + 1]);
            float posZ = Float.intBitsToFloat(vertices[posOffset + 2]);

            float u0 = Float.intBitsToFloat(vertices[uv0Offset + 0]);
            float v0 = Float.intBitsToFloat(vertices[uv0Offset + 1]);

            int packedColor = vertices[colorOffset];
            int packedLight = vertices[uv2Offset];
            int packedNormal = vertices[normalOffset];

            float normalX = ((byte) (packedNormal & 0xFF)) / 127.0f;
            float normalY = ((byte) ((packedNormal >> 8) & 0xFF)) / 127.0f;
            float normalZ = ((byte) ((packedNormal >> 16) & 0xFF)) / 127.0f;

            polygon[i] = new Vertex(
                    new Vector3f(posX, posY, posZ),
                    new Vector2f(u0, v0),
                    new Vector3f(normalX, normalY, normalZ),
                    packedColor,
                    packedLight
            );
        }

        if (!CullerUtils.shouldCull(
                polygon,
                texture,
                bufferGraph
        )) {
            for (int i = 0; i < size; i ++) {
                Vertex vertex = polygon[i];

                Vector3f vertexPosition = vertex.getPosition();
                Vector2f vertexUV = vertex.getUv();
                Vector3f vertexNormal = vertex.getNormal();

                meshBuilder.addVertex(
                        vertexPosition.x,
                        vertexPosition.y,
                        vertexPosition.z,
                        vertex.getColor(),
                        vertexUV.x,
                        vertexUV.y,
                        combinedOverlay,
                        vertex.getLight(),
                        vertexNormal.x,
                        vertexNormal.y,
                        vertexNormal.z
                );
            }
        }

        mesh = AcceleratedItemRenderingFeature.getMeshBuilder().build(meshCollector);

        meshes.put(bufferGraph, mesh);
        mesh.write(
                extension,
                getCustomColor(color),
                combinedLight,
                combinedOverlay
        );
    }

    @Unique
    @Override
    public int getCustomColor(int color) {
        return isTinted() ? color : -1;
    }
}

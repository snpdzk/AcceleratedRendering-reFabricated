package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshCollector;
import com.github.argon4w.acceleratedrendering.core.utils.*;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedQuad;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements IAcceleratedBakedQuad {

    @Unique private static final Map<int[], Map<TextureAtlasSprite, Map<RenderType, IMesh>>> SPRITE_MESHES = new LazyMap<>(new Reference2ObjectOpenHashMap<>(), LazyMap.supplierOf(Reference2ObjectOpenHashMap::new, Reference2ObjectOpenHashMap::new));

    @Shadow @Final protected int[] vertices;

    @Unique
    @Override
    public void renderFast(IAcceleratedVertexConsumer extension, int combinedLight, int combinedOverlay, int color) {
        RenderType renderType = extension.getRenderType();
        TextureAtlasSprite sprite = extension.getSprite();

        Map<RenderType, IMesh> meshes = SPRITE_MESHES.get(vertices).get(sprite);
        IMesh mesh = meshes.get(renderType);

        if (hasCustomColor()) {
            color = getCustomColor();
        }

        if (mesh != null) {
            mesh.write(
                    extension,
                    color,
                    combinedLight,
                    combinedOverlay
            );
            return;
        }

        MeshCollector meshCollector = AcceleratedItemRenderingFeature.getMeshBuilder().newMeshCollector(renderType);
        NativeImage image = TextureUtils.downloadTexture(renderType, 0);

        int size = vertices.length / 8;

        Vertex[] modelVertices = new Vertex[size];

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

            modelVertices[i] = new Vertex(
                    new Vector3f(posX, posY, posZ),
                    packedColor,
                    new Vector2f(sprite.getU(u0), sprite.getV(v0)),
                    new Vector3f(normalX, normalY, normalZ)
            );
        }

        if (!CullerUtils.shouldCull(modelVertices, image)) {
            for (int i = 0; i < size; i ++) {
                Vertex vertex = modelVertices[i];

                Vector3f position = vertex.getPosition();
                Vector2f uv = vertex.getUv();
                Vector3f normal = vertex.getNormal();

                meshCollector.addVertex(
                        position.x,
                        position.y,
                        position.z,
                        vertex.getColor(),
                        uv.x,
                        uv.y,
                        combinedOverlay,
                        combinedLight,
                        normal.x,
                        normal.y,
                        normal.z
                );
            }
        }

        mesh = meshCollector.build();

        meshes.put(renderType, mesh);
        mesh.write(
                extension,
                color,
                combinedLight,
                combinedOverlay
        );
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

package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshCollector;
import com.github.argon4w.acceleratedrendering.core.utils.CullerUtils;
import com.github.argon4w.acceleratedrendering.core.utils.TextureUtils;
import com.github.argon4w.acceleratedrendering.core.utils.UVUtils;
import com.github.argon4w.acceleratedrendering.core.utils.Vertex;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderContext;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(SimpleBakedModel.class)
public class SimpleBakedModelMixin implements IAcceleratedBakedModel, IAcceleratedRenderer<AcceleratedItemRenderContext> {

    @Shadow @Final protected List<BakedQuad> unculledFaces;
    @Shadow @Final protected Map<Direction, List<BakedQuad>> culledFaces;

    @Unique private final Map<RenderType, Int2ObjectMap<IMesh>> meshes = new Object2ObjectOpenHashMap<>();

    @Override
    public void renderItemFast(ItemStack itemStack, PoseStack poseStack, IAcceleratedVertexConsumer extension, int combinedLight, int combinedOverlay) {
        PoseStack.Pose pose = poseStack.last();

        extension.doRender(
                this,
                new AcceleratedItemRenderContext(
                        itemStack,
                        null,
                        null
                ),
                pose.pose(),
                pose.normal(),
                combinedLight,
                combinedOverlay,
                -1
        );
    }

    @Override
    public void render(
            VertexConsumer vertexConsumer,
            AcceleratedItemRenderContext context,
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            int light,
            int overlay,
            int color
    ) {
        ItemStack itemStack = context.getItemStack();
        ItemColor itemColor = context.getItemColor();
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) vertexConsumer;

        RenderType renderType = extension.getRenderType();
        Int2ObjectMap<IMesh> layers = meshes.get(renderType);

        extension.beginTransform(transformMatrix, normalMatrix);

        if (layers != null) {
            for (int layer : layers.keySet()) {
                IMesh mesh = layers.get(layer);

                mesh.write(
                        extension,
                        hasCustomColor() ? getCustomColor(layer) : itemColor.getColor(itemStack, layer),
                        light,
                        overlay
                );
            }

            extension.endTransform();
            return;
        }

        layers = new Int2ObjectLinkedOpenHashMap<>();
        meshes.put(renderType, layers);

        Int2ObjectMap<MeshCollector> meshCollectors = new Int2ObjectLinkedOpenHashMap<>();
        NativeImage image = TextureUtils.downloadTexture(renderType, 0);

        List<BakedQuad> allFaces = new ArrayList<>(unculledFaces);

        for (Direction direction : culledFaces.keySet()) {
            allFaces.addAll(culledFaces.get(direction));
        }

        for (BakedQuad quad : allFaces) {
            int[] vertices = quad.getVertices();
            int layer = quad.getTintIndex();
            int size = vertices.length / 8;

            MeshCollector meshCollector = meshCollectors.get(layer);

            if (meshCollector == null) {
                meshCollector = AcceleratedItemRenderingFeature.getMeshBuilder().newMeshCollector(renderType);
                meshCollectors.put(layer, meshCollector);
            }

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
                        UVUtils.getMapper(vertexConsumer),
                        new Vector3f(posX, posY, posZ),
                        packedColor,
                        new Vector2f(u0, v0),
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
                            overlay,
                            light,
                            normal.x,
                            normal.y,
                            normal.z
                    );
                }
            }
        }

        for (int layer : meshCollectors.keySet()) {
            IMesh mesh = meshCollectors.get(layer).build();

            layers.put(layer, mesh);
            mesh.write(
                    extension,
                    hasCustomColor() ? getCustomColor(layer) : itemColor.getColor(itemStack, layer),
                    light,
                    overlay
            );
        }

        extension.endTransform();
    }

    @Override
    public boolean isAccelerated() {
        return true;
    }

    @Override
    public boolean hasCustomColor() {
        return false;
    }

    @Override
    public int getCustomColor(int layer) {
        return -1;
    }
}

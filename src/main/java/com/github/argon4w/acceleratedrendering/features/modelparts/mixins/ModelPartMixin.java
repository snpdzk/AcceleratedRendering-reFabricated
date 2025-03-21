package com.github.argon4w.acceleratedrendering.features.modelparts.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshCollector;
import com.github.argon4w.acceleratedrendering.core.utils.CullerUtils;
import com.github.argon4w.acceleratedrendering.core.utils.TextureUtils;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.modelparts.VertexUtils;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public class ModelPartMixin implements IAcceleratedRenderer<Void> {

    @Shadow @Final private List<ModelPart.Cube> cubes;

    @Unique private final Map<RenderType, IMesh> meshes = new Object2ObjectOpenHashMap<>();

    @Inject(method = "compile", at = @At("HEAD"), cancellable = true)
    public void compile(
            PoseStack.Pose pPose,
            VertexConsumer pBuffer,
            int pPackedLight,
            int pPackedOverlay,
            int pColor,
            CallbackInfo ci
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) pBuffer;

        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        if (!AcceleratedEntityRenderingFeature.shouldUseAcceleratedPipeline()) {
            return;
        }

        if (!extension.isAccelerated()) {
            return;
        }

        ci.cancel();

        extension.doRender(
                this,
                null,
                pPose.pose(),
                pPose.normal(),
                pPackedLight,
                pPackedOverlay,
                pColor
        );
    }

    @Unique
    @Override
    public void render(
            VertexConsumer vertexConsumer,
            Void context,
            Matrix4f transformMatrix,
            Matrix3f normalMatrix,
            int light,
            int overlay,
            int color
    ) {
        IAcceleratedVertexConsumer extension = ((IAcceleratedVertexConsumer) vertexConsumer);

        RenderType renderType = extension.getRenderType();
        IMesh mesh = meshes.get(renderType);

        extension.beginTransform(transformMatrix, normalMatrix);

        if (mesh != null) {
            mesh.write(
                    extension,
                    color,
                    light,
                    overlay
            );
            return;
        }

        MeshCollector meshCollector = AcceleratedEntityRenderingFeature.getMeshBuilder().newMeshCollector(renderType);
        NativeImage image = TextureUtils.downloadTexture(renderType, 0);

        for (ModelPart.Cube cube : cubes) {
            for (ModelPart.Polygon polygon : cube.polygons) {
                Vector3f normal = polygon.normal;

                if (CullerUtils.shouldCull(VertexUtils.fromModelPart(polygon.vertices), image)) {
                    continue;
                }

                for (ModelPart.Vertex vertex : polygon.vertices) {
                    meshCollector.addVertex(
                            vertex.pos.x / 16.0f,
                            vertex.pos.y / 16.0f,
                            vertex.pos.z / 16.0f,
                            -1,
                            vertex.u,
                            vertex.v,
                            overlay,
                            0,
                            normal.x,
                            normal.y,
                            normal.z
                    );
                }
            }
        }

        mesh = meshCollector.build();

        meshes.put(renderType, mesh);
        mesh.write(
                extension,
                color,
                light,
                overlay
        );

        extension.endTransform();
    }
}

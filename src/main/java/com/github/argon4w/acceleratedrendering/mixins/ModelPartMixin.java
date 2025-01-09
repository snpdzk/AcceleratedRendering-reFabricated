package com.github.argon4w.acceleratedrendering.mixins;

import com.github.argon4w.acceleratedrendering.builders.IVertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.builders.IMesh;
import com.github.argon4w.acceleratedrendering.builders.MeshBuilder;
import com.github.argon4w.acceleratedrendering.utils.CullerUtils;
import com.github.argon4w.acceleratedrendering.utils.TextureUtils;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
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
import java.util.Optional;

@Mixin(ModelPart.class)
public class ModelPartMixin {

    @Shadow @Final private List<ModelPart.Cube> cubes;

    @Unique private final Map<RenderType, IMesh> acceleratedrendering$meshes = new Reference2ObjectOpenHashMap<>();

    @Inject(method = "compile", at = @At("HEAD"), cancellable = true)
    public void compile(PoseStack.Pose pPose, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor, CallbackInfo ci) {
        IVertexConsumerExtension extension = (IVertexConsumerExtension) pBuffer;

        if (!extension.acceleratedrendering$supportAcceleratedRendering()) {
            return;
        }

        extension.acceleratedrendering$beginTransform(pPose);

        RenderType renderType = extension.acceleratedrendering$getRenderType();
        IMesh mesh = acceleratedrendering$meshes.get(renderType);

        if (mesh != null) {
            mesh.render(extension, pColor, pPackedLight, pPackedOverlay);
            ci.cancel();
            return;
        }

        MeshBuilder meshBuilder = MeshBuilder.create(new ByteBufferBuilder(64));
        Optional<NativeImage> image = TextureUtils.downloadTexture(renderType);

        for (ModelPart.Cube cube : cubes) {
            for (ModelPart.Polygon polygon : cube.polygons) {
                Vector3f normal = polygon.normal;

                if (image.isPresent() && CullerUtils.shouldCull(polygon.vertices, image.get())) {
                    continue;
                }

                for (ModelPart.Vertex vertex : polygon.vertices) {
                    meshBuilder.addVertex(
                            vertex.pos.x / 16.0f,
                            vertex.pos.y / 16.0f,
                            vertex.pos.z / 16.0f,
                            pColor,
                            vertex.u,
                            vertex.v,
                            pPackedOverlay,
                            pPackedLight,
                            normal.x,
                            normal.y,
                            normal.z
                    );
                }
            }
        }

        mesh = meshBuilder.build();
        acceleratedrendering$meshes.put(renderType, mesh);
        mesh.render(extension, pColor, pPackedLight, pPackedOverlay);

        image.ifPresent(NativeImage::close);
        ci.cancel();
    }
}

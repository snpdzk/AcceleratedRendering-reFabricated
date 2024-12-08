package com.example.examplemod.mixins;

import com.example.examplemod.IBufferBuilderExtension;
import com.example.examplemod.PolygonCuller;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Mixin(ModelPart.class)
public class ModelPartMixin {
    @Shadow @Final private List<ModelPart.Cube> cubes;

    @Unique private final HashMap<RenderType, ByteBuffer> sme$cachedBuffers = new HashMap<>();
    @Unique private final HashMap<RenderType, Integer> sme$vertices = new HashMap<>();

    @Inject(method = "compile", at = @At("HEAD"), cancellable = true)
    public void compile(PoseStack.Pose pPose, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor, CallbackInfo ci) {
        IBufferBuilderExtension extension = (IBufferBuilderExtension) pBuffer;

        if (!extension.sme$supportAcceleratedRendering()) {
            return;
        }

        extension.sme$beginTransform(pPose);

        RenderType renderType = extension.sme$getRenderType();
        ByteBuffer cachedVertexBuffer = sme$cachedBuffers.get(renderType);
        int vertices = sme$vertices.getOrDefault(renderType, -1);

        if (vertices != -1) {
            extension.sme$addMesh(cachedVertexBuffer, vertices);
            ci.cancel();
            return;
        }

        BufferBuilder bufferBuilder = new BufferBuilder(new ByteBufferBuilder(64), VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        IBufferBuilderExtension extension2 = (IBufferBuilderExtension) bufferBuilder;
        Optional<NativeImage> image = PolygonCuller.downloadTexture(extension.sme$getRenderType());

        for (ModelPart.Cube cube : cubes) {
            for (ModelPart.Polygon polygon : cube.polygons) {
                Vector3f normal = polygon.normal;

                if (image.isPresent() && PolygonCuller.shouldCull(polygon.vertices, image.get())) {
                    continue;
                }

                for (ModelPart.Vertex vertex : polygon.vertices) {
                    bufferBuilder.addVertex(
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

        sme$vertices.put(renderType, extension2.sme$getVertices());
        MeshData meshData = bufferBuilder.build();

        if (meshData != null) {
            ByteBuffer byteBuffer = meshData.vertexBuffer();
            sme$cachedBuffers.put(renderType, byteBuffer);
            extension.sme$addMesh(byteBuffer, extension2.sme$getVertices());
        }

        image.ifPresent(NativeImage::close);
        ci.cancel();
    }
}

package com.example.examplemod.mixins;

import com.example.examplemod.CachedVertex;
import com.example.examplemod.IBufferBuilderExtension;
import com.example.examplemod.SMEBuffers;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;

import java.nio.ByteBuffer;
import java.util.List;

@Mixin(ModelPart.class)
public class ModelPartMixin {
    @Shadow @Final private List<ModelPart.Cube> cubes;

    @Unique private ByteBuffer sme$cachedBuffer;
    @Unique private int sme$vertices;

    /**
     * @author Argon4W
     * @reason Improve performance
     */
    @Overwrite
    public void compile(PoseStack.Pose pPose, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
        IBufferBuilderExtension extension = (IBufferBuilderExtension) pBuffer;
        extension.sme$beginTransform(pPose);

        if (sme$cachedBuffer != null) {
            extension.sme$addMesh(sme$cachedBuffer, sme$vertices);
            extension.sme$setTransformIndex(sme$vertices);
            return;
        }

        BufferBuilder bufferBuilder = new BufferBuilder(new ByteBufferBuilder(64), VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        IBufferBuilderExtension extension2 = (IBufferBuilderExtension) bufferBuilder;

        for (ModelPart.Cube cube : cubes) {
            for (ModelPart.Polygon polygon : cube.polygons) {
                Vector3f normal = polygon.normal;

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

        sme$vertices = extension2.sme$getVertices();
        sme$cachedBuffer = bufferBuilder.buildOrThrow().vertexBuffer();

        extension.sme$addMesh(sme$cachedBuffer, sme$vertices);
        extension.sme$setTransformIndex(sme$vertices);
    }
}

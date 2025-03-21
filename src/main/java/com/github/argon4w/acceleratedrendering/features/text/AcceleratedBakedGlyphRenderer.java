package com.github.argon4w.acceleratedrendering.features.text;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshCollector;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.text.mixins.BakedGlyphAccessor;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Map;

public class AcceleratedBakedGlyphRenderer implements IAcceleratedRenderer<Void> {

    private final Map<RenderType, IMesh> meshes;
    private final BakedGlyph glyph;
    private final boolean italic;

    public AcceleratedBakedGlyphRenderer(BakedGlyph glyph, boolean italic) {
        this.meshes = new Object2ObjectOpenHashMap<>();
        this.glyph = glyph;
        this.italic = italic;
    }

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
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) vertexConsumer;

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

            extension.endTransform();
            return;
        }

        IMesh.Builder builder = AcceleratedEntityRenderingFeature.getMeshBuilder();
        MeshCollector meshCollector = builder.newMeshCollector(renderType);

        float w1 = italic ? 1.0F - 0.25F * ((BakedGlyphAccessor) glyph).getUp() : 0.0F;
        float w2 = italic ? 1.0F - 0.25F * ((BakedGlyphAccessor) glyph).getDown() : 0.0F;

        meshCollector.addVertex(
                ((BakedGlyphAccessor) glyph).getLeft() + w1,
                ((BakedGlyphAccessor) glyph).getUp(),
                0.0F,
                -1,
                ((BakedGlyphAccessor) glyph).getU0(),
                ((BakedGlyphAccessor) glyph).getV0(),
                -1,
                0,
                -1,
                -1,
                -1
        );

        meshCollector.addVertex(
                ((BakedGlyphAccessor) glyph).getLeft() + w2,
                ((BakedGlyphAccessor) glyph).getDown(),
                0.0F,
                -1,
                ((BakedGlyphAccessor) glyph).getU0(),
                ((BakedGlyphAccessor) glyph).getV1(),
                -1,
                0,
                -1,
                -1,
                -1
        );

        meshCollector.addVertex(
                ((BakedGlyphAccessor) glyph).getRight() + w2,
                ((BakedGlyphAccessor) glyph).getDown(),
                0.0F,
                -1,
                ((BakedGlyphAccessor) glyph).getU1(),
                ((BakedGlyphAccessor) glyph).getV1(),
                -1,
                0,
                -1,
                -1,
                -1
        );

        meshCollector.addVertex(
                ((BakedGlyphAccessor) glyph).getRight() + w1,
                ((BakedGlyphAccessor) glyph).getUp(),
                0.0F,
                -1,
                ((BakedGlyphAccessor) glyph).getU1(),
                ((BakedGlyphAccessor) glyph).getV0(),
                -1,
                0,
                -1,
                -1,
                -1
        );

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

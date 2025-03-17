package com.github.argon4w.acceleratedrendering.features.text.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshCollector;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.text.AcceleratedTextRenderingFeature;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = BakedGlyph.class, priority = Integer.MIN_VALUE)
public class BakedGlyphMixin {

    @Unique private static final Matrix4f TRANSFORM = new Matrix4f();
    @Unique private static final Matrix3f NORMAL = new Matrix3f();

    @Shadow @Final private float left;
    @Shadow @Final private float right;
    @Shadow @Final private float up;
    @Shadow @Final private float down;
    @Shadow @Final private float u0;
    @Shadow @Final private float v0;
    @Shadow @Final private float u1;
    @Shadow @Final private float v1;

    @Unique private final Map<RenderType, IMesh> normalMeshes = new Object2ObjectOpenHashMap<>();
    @Unique private final Map<RenderType, IMesh> italicMeshes = new Object2ObjectOpenHashMap<>();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(
            boolean pItalic,
            float pX,
            float pY,
            Matrix4f pMatrix,
            VertexConsumer pBuffer,
            float pRed,
            float pGreen,
            float pBlue,
            float pAlpha,
            int pPackedLight,
            CallbackInfo ci
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) pBuffer;
        Map<RenderType, IMesh> meshes = pItalic
                ? italicMeshes
                : normalMeshes;

        if (!AcceleratedTextRenderingFeature.isEnabled()) {
            return;
        }

        if (!AcceleratedTextRenderingFeature.shouldUseAcceleratedPipeline()) {
            return;
        }

        if (!extension.isAccelerated()) {
            return;
        }

        ci.cancel();

        TRANSFORM
                .set(pMatrix)
                .translate(pX, pY, 0.0f);

        extension.beginTransform(TRANSFORM, NORMAL);

        int color = FastColor.ABGR32.color(
                (int) (pAlpha * 255.0F),
                (int) (pBlue * 255.0F),
                (int) (pGreen * 255.0F),
                (int) (pRed * 255.0F)
        );

        for (RenderType renderType : extension.getRenderTypes()) {
            IMesh mesh = meshes.get(renderType);

            if (mesh != null) {
                mesh.write(
                        extension,
                        color,
                        pPackedLight,
                        -1
                );
                continue;
            }

            IMesh.Builder builder = AcceleratedEntityRenderingFeature.getMeshBuilder();
            MeshCollector meshCollector = builder.newMeshCollector(renderType);

            float w1 = pItalic ? 1.0F - 0.25F * this.up : 0.0F;
            float w2 = pItalic ? 1.0F - 0.25F * this.down : 0.0F;

            meshCollector.addVertex(
                    this.left + w1,
                    this.up,
                    0.0F,
                    -1,
                    this.u0,
                    this.v0,
                    -1,
                    pPackedLight,
                    -1,
                    -1,
                    -1
            );

            meshCollector.addVertex(
                    this.left + w2,
                    this.down,
                    0.0F,
                    -1,
                    this.u0,
                    this.v1,
                    -1,
                    pPackedLight,
                    -1,
                    -1,
                    -1
            );

            meshCollector.addVertex(
                    this.right + w2,
                    this.down,
                    0.0F,
                    -1,
                    this.u1,
                    this.v1,
                    -1,
                    pPackedLight,
                    -1,
                    -1,
                    -1
            );

            meshCollector.addVertex(
                    this.right + w1,
                    this.up,
                    0.0F,
                    -1,
                    this.u1,
                    this.v0,
                    -1,
                    pPackedLight,
                    -1,
                    -1,
                    -1
            );

            mesh = builder.build(meshCollector);

            meshes.put(renderType, mesh);
            mesh.write(
                    extension,
                    color,
                    pPackedLight,
                    -1
            );
        }

        extension.endTransform();
    }
}

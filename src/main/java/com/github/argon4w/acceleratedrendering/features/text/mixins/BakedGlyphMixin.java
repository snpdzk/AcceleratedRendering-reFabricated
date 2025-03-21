package com.github.argon4w.acceleratedrendering.features.text.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.features.text.AcceleratedBakedGlyphRenderer;
import com.github.argon4w.acceleratedrendering.features.text.AcceleratedTextRenderingFeature;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BakedGlyph.class, priority = Integer.MIN_VALUE)
public class BakedGlyphMixin {

    @Unique private static final Matrix4f TRANSFORM = new Matrix4f();
    @Unique private static final Matrix3f NORMAL = new Matrix3f();


    @Unique private final AcceleratedBakedGlyphRenderer normalRenderer = new AcceleratedBakedGlyphRenderer((BakedGlyph) (Object) this, false);
    @Unique private final AcceleratedBakedGlyphRenderer italicRenderer = new AcceleratedBakedGlyphRenderer((BakedGlyph) (Object) this, true);

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

        int color = FastColor.ABGR32.color(
                (int) (pAlpha * 255.0F),
                (int) (pBlue * 255.0F),
                (int) (pGreen * 255.0F),
                (int) (pRed * 255.0F)
        );

        AcceleratedBakedGlyphRenderer renderer = pItalic
                ? italicRenderer
                : normalRenderer;

        extension.doRender(
                renderer,
                null,
                TRANSFORM,
                NORMAL,
                pPackedLight,
                0,
                color
        );
    }
}

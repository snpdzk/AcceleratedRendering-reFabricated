package com.github.argon4w.acceleratedrendering.core.buffers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;

public class SimpleCrumblingBufferSource implements MultiBufferSource {

    private final MultiBufferSource bufferSource;
    private final VertexConsumer crumblingBuffer;

    public SimpleCrumblingBufferSource(MultiBufferSource bufferSource, VertexConsumer crumblingBuffer) {
        this.bufferSource = bufferSource;
        this.crumblingBuffer = crumblingBuffer;
    }

    public SimpleCrumblingBufferSource(
            MultiBufferSource bufferSource,
            int progress,
            PoseStack poseStack,
            float textureScale
    ) {
        this(bufferSource, new SheetedDecalTextureGenerator(
                bufferSource.getBuffer(ModelBakery.DESTROY_TYPES.get(progress)),
                poseStack.last(),
                textureScale
        ));
    }

    @Override
    public VertexConsumer getBuffer(RenderType pRenderType) {
        VertexConsumer buffer = bufferSource.getBuffer(pRenderType);

        if (!pRenderType.affectsCrumbling) {
            return buffer;
        }

        return VertexMultiConsumer.create(crumblingBuffer, buffer);
    }
}

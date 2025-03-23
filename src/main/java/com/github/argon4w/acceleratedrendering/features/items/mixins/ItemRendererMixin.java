package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.mixins.MCItemColorsAccessor;
import com.github.argon4w.acceleratedrendering.core.utils.DirectionUtils;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderContext;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.EmptyItemColor;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedQuad;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin implements IAcceleratedRenderer<AcceleratedItemRenderContext> {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    public void renderFast(
            ItemRenderer instance,
            BakedModel pModel,
            ItemStack pStack,
            int pCombinedLight,
            int pCombinedOverlay,
            PoseStack pPoseStack,
            VertexConsumer pBuffer,
            Operation<Void> original
    ) {
        IAcceleratedVertexConsumer extension1 = (IAcceleratedVertexConsumer) pBuffer;
        IAcceleratedBakedModel extension2 = (IAcceleratedBakedModel) pModel;

        if (!AcceleratedItemRenderingFeature.isEnabled()) {
            original.call(
                    instance,
                    pModel,
                    pStack,
                    pCombinedLight,
                    pCombinedOverlay,
                    pPoseStack,
                    pBuffer
            );
            return;
        }

        if (!AcceleratedItemRenderingFeature.shouldUseAcceleratedPipeline()) {
            original.call(
                    instance,
                    pModel,
                    pStack,
                    pCombinedLight,
                    pCombinedOverlay,
                    pPoseStack,
                    pBuffer
            );
            return;
        }

        if (!extension1.isAccelerated()) {
            original.call(
                    instance,
                    pModel,
                    pStack,
                    pCombinedLight,
                    pCombinedOverlay,
                    pPoseStack,
                    pBuffer
            );
            return;
        }

        if (extension2.isAccelerated()) {
            extension2.renderItemFast(
                    pStack,
                    pPoseStack,
                    extension1,
                    pCombinedLight,
                    pCombinedOverlay
            );
            return;
        }

        if (!AcceleratedItemRenderingFeature.shouldBakeMeshForQuad()) {
            original.call(
                    instance,
                    pModel,
                    pStack,
                    pCombinedLight,
                    pCombinedOverlay,
                    pPoseStack,
                    pBuffer
            );
            return;
        }

        PoseStack.Pose pose = pPoseStack.last();

        extension1.doRender(
                this,
                new AcceleratedItemRenderContext(
                        pStack,
                        pModel,
                        RandomSource.create()
                ),
                pose.pose(),
                pose.normal(),
                pCombinedLight,
                pCombinedOverlay,
                -1
        );
    }

    @SuppressWarnings("deprecation")
    @Unique
    @Override
    public void render(
            VertexConsumer vertexConsumer,
            AcceleratedItemRenderContext context,
            Matrix4f transform,
            Matrix3f normal,
            int light,
            int overlay,
            int color
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) vertexConsumer;

        ItemStack itemStack = context.getItemStack();
        ItemColor itemColor = context.getItemColor();
        BakedModel model = context.getBakedModel();
        RandomSource source = context.getRandom();

        extension.beginTransform(transform, normal);

        for (Direction direction : DirectionUtils.FULL) {
            source.setSeed(42L);

            for (BakedQuad quad : model.getQuads(null, direction, source)) {
                ((IAcceleratedBakedQuad) quad).renderFast(
                        transform,
                        normal,
                        extension,
                        light,
                        overlay,
                        itemColor.getColor(itemStack, quad.getTintIndex())
                );
            }
        }

        extension.endTransform();
    }
}

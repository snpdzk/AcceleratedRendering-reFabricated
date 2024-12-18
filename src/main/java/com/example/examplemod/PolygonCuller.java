package com.example.examplemod;

import com.mojang.blaze3d.platform.NativeImage;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Optional;

import static org.lwjgl.opengl.GL46.*;

public class PolygonCuller {

    public static boolean shouldCull(ModelPart.Vertex[] vertices, NativeImage image) {
        float minU = 1.0f;
        float minV = 1.0f;

        float maxU = 0.0f;
        float maxV = 0.0f;

        for (ModelPart.Vertex vertex : vertices) {
            float u = vertex.u;
            float v = vertex.v;

            u = u < 0 ? 1.0f + u : u;
            v = v < 0 ? 1.0f + v : v;

            minU = Math.min(minU, u);
            minV = Math.min(minV, v);
            maxU = Math.max(maxU, u);
            maxV = Math.max(maxV, v);
        }

        for (int x = Mth.floor(minU * image.getWidth()); x < Mth.ceil(maxU * image.getWidth()); x++) {
            for (int y = Mth.floor(minV * image.getHeight()); y < Mth.ceil(maxV * image.getHeight()); y++) {
                if ((image.getPixelRGBA(x, y) & 0xFF) != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public static Optional<NativeImage> downloadTexture(RenderType renderType) {
        if (renderType instanceof WrappableRenderType wrappable) {
            renderType = wrappable.unwrap();
        }

        if (!(renderType instanceof RenderType.CompositeRenderType composite)) {
            return Optional.empty();
        }

        Optional<ResourceLocation> textureResourceLocation = composite.state.textureState.cutoutTexture();

        if (textureResourceLocation.isEmpty()) {
            return Optional.empty();
        }

        Minecraft.getInstance().getTextureManager().getTexture(textureResourceLocation.get()).bind();

        int[] width = new int[1];
        int[] height = new int[1];

        glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, width);
        glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, height);

        if (width[0] == 0 || height[0] == 0) {
            return Optional.empty();
        }

        NativeImage nativeImage = new NativeImage(width[0], height[0], false);
        nativeImage.downloadTexture(0, false);

        return Optional.of(nativeImage);
    }
}

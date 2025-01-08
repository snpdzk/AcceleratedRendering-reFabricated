package com.github.argon4w.acceleratedrendering.utils;

import com.github.argon4w.acceleratedrendering.compat.IrisCompat;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;

public class TextureUtils {

    public static Optional<NativeImage> downloadTexture(RenderType renderType) {
        if (renderType == null) {
            return Optional.empty();
        }

        renderType = IrisCompat.unwrapRenderType(renderType);

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

package com.github.argon4w.acceleratedrendering.core.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;

public class TextureUtils {

    public static Optional<NativeImage> downloadTexture(RenderType renderType) {
        Optional<ResourceLocation> textureResourceLocation = RenderTypeUtils.getTextureLocation(renderType);

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

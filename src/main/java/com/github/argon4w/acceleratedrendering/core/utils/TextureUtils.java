package com.github.argon4w.acceleratedrendering.core.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static org.lwjgl.opengl.GL46.*;

public class TextureUtils {

    public static NativeImage downloadTexture(RenderType renderType) {
        ResourceLocation textureResourceLocation = RenderTypeUtils.getTextureLocation(renderType);

        if (textureResourceLocation == null) {
            return null;
        }

        Minecraft.getInstance().getTextureManager().getTexture(textureResourceLocation).bind();

        int[] width = new int[1];
        int[] height = new int[1];

        glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, width);
        glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, height);

        if (width[0] == 0 || height[0] == 0) {
            return null;
        }

        NativeImage nativeImage = new NativeImage(width[0], height[0], false);
        nativeImage.downloadTexture(0, false);

        return nativeImage;
    }
}

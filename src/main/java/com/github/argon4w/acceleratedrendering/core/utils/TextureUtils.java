package com.github.argon4w.acceleratedrendering.core.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

public class TextureUtils {

    public static NativeImage downloadTexture(RenderType renderType) {
        ResourceLocation textureResourceLocation = RenderTypeUtils.getTextureLocation(renderType);

        if (textureResourceLocation == null) {
            return null;
        }

        Minecraft
                .getInstance()
                .getTextureManager()
                .getTexture(textureResourceLocation)
                .bind();

        IntBuffer widthBuffer = MemoryUtil.memCallocInt(1);
        IntBuffer heightBuffer = MemoryUtil.memCallocInt(1);

        glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, widthBuffer);
        glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, heightBuffer);

        int width = widthBuffer.get(0);
        int height = heightBuffer.get(0);

        if (width == 0) {
            return null;
        }

        if (height == 0) {
            return null;
        }

        NativeImage nativeImage = new NativeImage(
                width,
                height,
                false
        );

        nativeImage.downloadTexture(0, false);
        MemoryUtil.memFree(widthBuffer);
        MemoryUtil.memFree(heightBuffer);

        return nativeImage;
    }
}

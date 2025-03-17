package com.github.argon4w.acceleratedrendering.core.utils;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

public class TextureUtils {

    private static final Object2ObjectLinkedOpenHashMap<ResourceLocation, NativeImage> IMAGES = new Object2ObjectLinkedOpenHashMap<>();

    public static NativeImage downloadTexture(RenderType renderType, int mipmapLevel) {
        ResourceLocation textureResourceLocation = RenderTypeUtils.getTextureLocation(renderType);

        if (textureResourceLocation == null) {
            return null;
        }


        NativeImage image = IMAGES.getAndMoveToFirst(textureResourceLocation);

        if (image != null) {
            return image;
        }

        Minecraft
                .getInstance()
                .getTextureManager()
                .getTexture(textureResourceLocation)
                .bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.callocInt(1);
            IntBuffer heightBuffer = stack.callocInt(1);

            glGetTexLevelParameteriv(
                    GL_TEXTURE_2D,
                    mipmapLevel,
                    GL_TEXTURE_WIDTH,
                    widthBuffer
            );

            glGetTexLevelParameteriv(
                    GL_TEXTURE_2D,
                    mipmapLevel,
                    GL_TEXTURE_HEIGHT,
                    heightBuffer
            );

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

            nativeImage.downloadTexture(mipmapLevel, false);

            IMAGES.put(textureResourceLocation, nativeImage);

            if (IMAGES.size() > CoreFeature.getCachedImageSize()) {
                IMAGES.removeLast().close();
            }

            return nativeImage;
        }
    }
}

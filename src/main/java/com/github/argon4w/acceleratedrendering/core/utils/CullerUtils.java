package com.github.argon4w.acceleratedrendering.core.utils;

import com.github.argon4w.acceleratedrendering.core.buffers.graphs.IBufferGraph;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CullerUtils {

    public static boolean shouldCull(
            Vertex[] vertices,
            NativeImage image,
            IBufferGraph graph
    ) {
        if (image == null) {
            return false;
        }

        float minU = 1.0f;
        float minV = 1.0f;

        float maxU = 0.0f;
        float maxV = 0.0f;

        if (vertices.length == 4) {
            Vector3f vertex0 = new Vector3f(vertices[0].getPosition());
            Vector3f vector1 = new Vector3f(vertices[1].getPosition()).sub(vertex0);
            Vector3f vector2 = new Vector3f(vertices[2].getPosition()).sub(vertex0);
            Vector3f vector3 = new Vector3f(vertices[3].getPosition()).sub(vertex0);

            float length1 = vector1.cross(vector2).length();
            float length2 = vector1.cross(vector3).length();

            if (length1 == 0 && length2 == 0) {
                return true;
            }
        }

        for (Vertex vertex : vertices) {
            Vector2f uv = vertex.getUv();

            float u = graph.mapU(uv.x);
            float v = graph.mapV(uv.y);

            u = u < 0 ? 1.0f + u : u;
            v = v < 0 ? 1.0f + v : v;

            minU = Math.min(minU, u);
            minV = Math.min(minV, v);
            maxU = Math.max(maxU, u);
            maxV = Math.max(maxV, v);
        }

        int minX = Math.max(0, Mth.floor(minU * image.getWidth()));
        int minY = Math.max(0, Mth.floor(minV * image.getHeight()));

        int maxX = Math.min(image.getWidth(), Mth.ceil(maxU * image.getWidth()));
        int maxY = Math.min(image.getHeight(), Mth.ceil(maxV * image.getHeight()));

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if (FastColor.ABGR32.alpha(image.getPixelRGBA(x, y)) != 0) {
                    return false;
                }
            }
        }

        return true;
    }
}

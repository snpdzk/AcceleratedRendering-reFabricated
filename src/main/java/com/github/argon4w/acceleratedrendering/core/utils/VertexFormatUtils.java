package com.github.argon4w.acceleratedrendering.core.utils;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

public class VertexFormatUtils {

    private static final Reference2IntMap<VertexFormat> CACHE = new Reference2IntOpenHashMap<>();

    public static int hashCodeFast(VertexFormat format) {
        int result = CACHE.getInt(format);

        if (result == 0) {
            result = format.hashCode();
            CACHE.put(format, result);
        }

        return result;
    }
}

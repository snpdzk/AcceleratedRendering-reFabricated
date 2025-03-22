package com.github.argon4w.acceleratedrendering.features.modelparts;

import com.github.argon4w.acceleratedrendering.core.utils.IUVMapper;
import com.github.argon4w.acceleratedrendering.core.utils.Vertex;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class VertexUtils {

    public static Vertex[] fromModelPart(ModelPart.Vertex[] vertices, IUVMapper mapper) {
        Vertex[] result = new Vertex[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            ModelPart.Vertex vertex = vertices[i];

            result[i] = new Vertex(
                    mapper,
                    vertex.pos,
                    vertex.u,
                    vertex.v
            );
        }

        return result;
    }
}

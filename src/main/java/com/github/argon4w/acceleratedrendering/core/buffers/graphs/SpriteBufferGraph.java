package com.github.argon4w.acceleratedrendering.core.buffers.graphs;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Objects;

public class SpriteBufferGraph implements IBufferGraph {

    private final IBufferGraph parent;
    private final TextureAtlasSprite sprite;

    public SpriteBufferGraph(IBufferGraph parent, TextureAtlasSprite sprite) {
        this.parent = parent;
        this.sprite = sprite;
    }

    @Override
    public float mapU(float u) {
        return parent.mapU(sprite.getU(u));
    }

    @Override
    public float mapV(float v) {
        return parent.mapV(sprite.getV(v));
    }

    @Override
    public RenderType getRenderType() {
        return parent.getRenderType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, sprite);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        SpriteBufferGraph that = (SpriteBufferGraph) obj;

        return Objects.equals(parent, that.parent)
                && Objects.equals(sprite, that.sprite);
    }
}

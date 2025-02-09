package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.neoforged.fml.ModLoader;

public interface ITransformProgramSelector {

    TransformProgramDispatcher select();

    static ITransformProgramSelector throwing() {
        return ThrowingTransformProgramSelector.INSTANCE;
    }

    static ITransformProgramSelector get(VertexFormat vertexFormat) {
        return ModLoader.postEventWithReturn(new LoadTransformProgramSelectorEvent(vertexFormat)).selector;
    }
}

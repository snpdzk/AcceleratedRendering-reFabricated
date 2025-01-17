package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.neoforged.fml.ModLoader;

public interface ITransformProgramSelector {

    Program select(VertexFormat vertexFormat);
    int getSharingFlags();

    static ITransformProgramSelector throwing() {
        return ThrowingTransformProgramSelector.INSTANCE;
    }

    static ITransformProgramSelector get(VertexFormat vertexFormat) {
        return ModLoader.postEventWithReturn(new LoadTransformProgramSelectorEvent(vertexFormat)).selector;
    }
}

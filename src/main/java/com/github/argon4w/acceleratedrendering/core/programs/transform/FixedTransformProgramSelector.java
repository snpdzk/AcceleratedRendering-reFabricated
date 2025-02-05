package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import net.minecraft.resources.ResourceLocation;

public class FixedTransformProgramSelector implements ITransformProgramSelector {

    private final ITransformProgramSelector parent;
    private final ComputeProgram program;

    public FixedTransformProgramSelector(ITransformProgramSelector parent, ComputeProgram program) {
        this.parent = parent;
        this.program = program;
    }

    public FixedTransformProgramSelector(ITransformProgramSelector parent, ResourceLocation key) {
        this(parent, ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public ComputeProgram select() {
        return program;
    }

    @Override
    public int getSharingFlags() {
        return parent.getSharingFlags();
    }
}

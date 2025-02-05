package com.github.argon4w.acceleratedrendering.compat.iris.programs.transform;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.transform.ITransformProgramSelector;
import net.minecraft.resources.ResourceLocation;

public class IrisTransformProgramSelector implements ITransformProgramSelector {

    private final ITransformProgramSelector parent;
    private final ComputeProgram program;

    public IrisTransformProgramSelector(ITransformProgramSelector parent, ComputeProgram program) {
        this.parent = parent;
        this.program = program;
    }

    public IrisTransformProgramSelector(ITransformProgramSelector parent, ResourceLocation key) {
        this(parent, ComputeShaderProgramLoader.getProgram(key));
    }

    @Override
    public ComputeProgram select() {
        return IrisCompatFeature.isEnabled()
                ? program
                : parent.select();
    }

    @Override
    public int getSharingFlags() {
        return parent.getSharingFlags();
    }
}

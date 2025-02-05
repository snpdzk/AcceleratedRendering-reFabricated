package com.github.argon4w.acceleratedrendering.core.programs.transform;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;

public class ThrowingTransformProgramSelector implements ITransformProgramSelector {

    public static final ThrowingTransformProgramSelector INSTANCE = new ThrowingTransformProgramSelector();

    @Override
    public ComputeProgram select() {
        throw new IllegalStateException("Cannot select a valid transform program.");
    }

    @Override
    public int getSharingFlags() {
        return 0;
    }
}

package com.github.argon4w.acceleratedrendering.core.programs.transform;

public class ThrowingTransformProgramSelector implements ITransformProgramSelector {

    public static final ThrowingTransformProgramSelector INSTANCE = new ThrowingTransformProgramSelector();

    @Override
    public TransformProgramDispatcher select() {
        throw new IllegalStateException("Cannot select a valid transform program dispatcher.");
    }
}

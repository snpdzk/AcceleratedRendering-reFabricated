package com.github.argon4w.acceleratedrendering.core.programs.transform;

import net.minecraft.resources.ResourceLocation;

public class FixedTransformProgramSelector implements ITransformProgramSelector {

    private final TransformProgramDispatcher dispatcher;

    public FixedTransformProgramSelector(TransformProgramDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public FixedTransformProgramSelector(ResourceLocation key) {
        this(new TransformProgramDispatcher(key));
    }

    @Override
    public TransformProgramDispatcher select() {
        return dispatcher;
    }
}

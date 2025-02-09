package com.github.argon4w.acceleratedrendering.core.programs.transform;

import net.minecraft.resources.ResourceLocation;

public class FixedTransformProgramSelector implements ITransformProgramSelector {

    private final ITransformProgramSelector parent;
    private final TransformProgramDispatcher dispatcher;

    public FixedTransformProgramSelector(ITransformProgramSelector parent, TransformProgramDispatcher dispatcher) {
        this.parent = parent;
        this.dispatcher = dispatcher;
    }

    public FixedTransformProgramSelector(ITransformProgramSelector parent, ResourceLocation key) {
        this(parent, new TransformProgramDispatcher(key));
    }

    @Override
    public TransformProgramDispatcher select() {
        return dispatcher;
    }

    @Override
    public int getSharingFlags() {
        return parent.getSharingFlags();
    }
}

package com.github.argon4w.acceleratedrendering.compat.iris.programs.transform;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatFeature;
import com.github.argon4w.acceleratedrendering.core.programs.transform.ITransformProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.transform.TransformProgramDispatcher;
import net.minecraft.resources.ResourceLocation;

public class IrisTransformProgramSelector implements ITransformProgramSelector {

    private final ITransformProgramSelector parent;
    private final TransformProgramDispatcher dispatcher;

    public IrisTransformProgramSelector(ITransformProgramSelector parent, TransformProgramDispatcher dispatcher) {
        this.parent = parent;
        this.dispatcher = dispatcher;
    }

    public IrisTransformProgramSelector(ITransformProgramSelector parent, ResourceLocation key) {
        this(parent, new TransformProgramDispatcher(key));
    }

    @Override
    public TransformProgramDispatcher select() {
        return IrisCompatFeature.isEnabled()
                ? dispatcher
                : parent.select();
    }

    @Override
    public int getSharingFlags() {
        return parent.getSharingFlags();
    }
}

package com.github.argon4w.acceleratedrendering.core.programs;

public class EmptyProgramDispatcher implements IPolygonProgramDispatcher {

    public static final EmptyProgramDispatcher INSTANCE = new EmptyProgramDispatcher();

    @Override
    public int dispatch(int vertexCount, int vertexOffset) {
        return 0;
    }
}

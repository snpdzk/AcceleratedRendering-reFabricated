package com.github.argon4w.acceleratedrendering.core.programs.extras;

import org.lwjgl.system.MemoryUtil;

public class FlagsExtraVertexData implements IExtraVertexData {

    private int data;

    public FlagsExtraVertexData() {
        this.data = 0;
    }

    public FlagsExtraVertexData(int bit) {
        this.data = 1 << bit;
    }

    public void set(int bit) {
        data |= 1 << bit;
    }

    public void reset(int bit) {
        data &= ~(1 << bit);
    }

    public void clear() {
        data = 0;
    }

    @Override
    public void addExtraVarying(long address) {
        MemoryUtil.memPutInt(address + 3L * 4L, data);
    }

    @Override
    public void addExtraVertex(long address) {

    }
}

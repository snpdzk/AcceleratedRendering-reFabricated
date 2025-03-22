package com.github.argon4w.acceleratedrendering.core.buffers.accelerated;

import com.github.argon4w.acceleratedrendering.core.backends.buffers.ImmutableBuffer;
import com.github.argon4w.acceleratedrendering.core.backends.buffers.MappedBuffer;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

public class AcceleratedDecalRotationBuffer extends ImmutableBuffer {

    public static final long SIZE = 4L * 4L * 4L * 6L;
    public static final int BITS = MappedBuffer.VERB_FLUSH_BITS;
    public static final int MAP_BITS = MappedBuffer.VERB_FLUSH_MAP_BITS;

    public AcceleratedDecalRotationBuffer(Direction[] directions) {
        super(SIZE, BITS);

        long address = map(SIZE, MAP_BITS);

        for (int i = 0; i < 6; i++) {
            new Matrix4f()
                    .rotate(directions[i].getRotation())
                    .rotateX((float) (- Math.PI / 2))
                    .rotateY((float) Math.PI)
                    .getToAddress(address + i * 4L * 4L * 4L);
        }

        flush(SIZE);
    }
}

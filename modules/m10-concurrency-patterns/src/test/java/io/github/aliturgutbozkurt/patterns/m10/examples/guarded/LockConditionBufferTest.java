package io.github.aliturgutbozkurt.patterns.m10.examples.guarded;

import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer.BoundedBuffer;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer.LockConditionBuffer;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class LockConditionBufferTest extends BoundedBufferContract {

    @Override
    protected <T> BoundedBuffer<T> newBuffer(int capacity) {
        return new LockConditionBuffer<>(capacity);
    }
}

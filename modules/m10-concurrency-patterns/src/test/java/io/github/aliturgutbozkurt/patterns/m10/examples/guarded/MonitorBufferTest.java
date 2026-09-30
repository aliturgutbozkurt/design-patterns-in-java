package io.github.aliturgutbozkurt.patterns.m10.examples.guarded;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer.BoundedBuffer;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer.MonitorBuffer;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class MonitorBufferTest extends BoundedBufferContract {

    @Override
    protected <T> BoundedBuffer<T> newBuffer(int capacity) {
        return new MonitorBuffer<>(capacity);
    }

    @Test
    void demoHandsEveryReadingOverInOrderWithBothImplementations() {
        assertThat(Demos.output(() -> BoundedBufferDemo.main(new String[0]))).isEqualTo("""
                LockConditionBuffer, capacity 2: writer stored [r1, r2, r3, r4, r5, r6, r7, r8]
                MonitorBuffer, capacity 2: writer stored [r1, r2, r3, r4, r5, r6, r7, r8]
                poll(50 ms) on an empty buffer: Optional.empty
                """);
    }
}

package io.github.aliturgutbozkurt.patterns.m10.examples.structured;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m10.examples.structured.context.ScopeRules;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.context.ScopedFanOut;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** Preview API (JEP 533): compiled and run with {@code --enable-preview} by this module's POM. */
@Timeout(10)
class StructuredContextTest {

    @Test
    void everyForkedSubtaskSeesTheParentsScopedValueBinding() throws InterruptedException {
        assertThat(ScopedFanOut.traceAll("trace-42", List.of("inventory", "pricing", "shipping")))
                .containsExactly("trace-42 -> inventory", "trace-42 -> pricing", "trace-42 -> shipping");
        assertThat(ScopedFanOut.TRACE_ID.isBound()).isFalse();
    }

    @Test
    void forkFromANonOwnerThreadThrowsWrongThreadException() throws InterruptedException {
        assertThat(ScopeRules.forkFromAnotherThread()).isInstanceOf(WrongThreadException.class);
    }

    @Test
    void forkAfterJoinThrowsIllegalStateException() throws InterruptedException {
        assertThat(ScopeRules.forkAfterJoin()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void subtaskGetBeforeJoinThrowsIllegalStateException() throws InterruptedException {
        assertThat(ScopeRules.getBeforeJoin()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void closingAScopeWithForksButWithoutJoinThrowsIllegalStateException() {
        assertThat(ScopeRules.closeWithoutJoin()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void demoPrintsInheritanceAndTheLifecycleRules() {
        assertThat(Demos.output(() -> StructuredContextDemo.main(new String[0]))).isEqualTo("""
                subtasks of a StructuredTaskScope: [trace-42 -> inventory, trace-42 -> pricing, trace-42 -> shipping]
                a task of newVirtualThreadPerTaskExecutor: bound = false
                fork from another thread -> WrongThreadException
                fork after join -> IllegalStateException
                Subtask.get() before join -> IllegalStateException
                close() after fork without join -> IllegalStateException
                """);
    }
}

package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal.ContextLeaks;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal.InheritanceFacts;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal.ThreadLocalContext;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class ThreadLocalVsScopedValueTest {

    @Test
    void forgottenRemoveLeaksTheFirstRequestsUserIntoTheNextTaskOnAPooledThread() throws Exception {
        assertThat(ContextLeaks.secondTaskSeesWithoutRemove()).isEqualTo("alice");   // the leak
    }

    @Test
    void removeInFinallyPreventsTheLeak() throws Exception {
        assertThat(ContextLeaks.secondTaskSeesWithFinallyRemove()).isNull();
    }

    @Test
    void threadLocalContextIsPerThread() throws InterruptedException {
        var context = new ThreadLocalContext();
        context.set("alice");
        String[] seenByOther = new String[1];
        Thread other = Thread.ofVirtual().start(() -> seenByOther[0] = context.get());
        other.join();
        assertThat(seenByOther[0]).isNull();
        assertThat(context.get()).isEqualTo("alice");
        context.remove();
        assertThat(context.get()).isNull();
    }

    @Test
    void inheritableThreadLocalIsCopiedIntoAChildVirtualThreadButAPlainOneIsNot() throws InterruptedException {
        assertThat(InheritanceFacts.inheritableSeenByChild("alice")).isEqualTo("alice");
        assertThat(InheritanceFacts.plainSeenByChild("alice")).isNull();
    }

    @Test
    void scopedValueBoundInTheParentIsUnboundInExecutorTasksAndRawThreads() throws Exception {
        assertThat(InheritanceFacts.scopedValueBoundInExecutorTask()).isFalse();
        assertThat(InheritanceFacts.scopedValueBoundInRawVirtualThread()).isFalse();
        assertThat(InheritanceFacts.scopedValueBoundAfterRun()).isFalse();
    }

    @Test
    void demoPrintsTheThreeWays() {
        assertThat(Demos.output(() -> ThreadLocalVsScopedValueDemo.main(new String[0]))).isEqualTo("""
                ThreadLocal, 1-thread pool, no remove(): task 2 sees user = alice   <- leaked from task 1
                ThreadLocal, 1-thread pool, remove() in finally: task 2 sees user = null
                InheritableThreadLocal in a child virtual thread: alice
                ThreadLocal in a child virtual thread: null
                ScopedValue bound in the parent, read in a newVirtualThreadPerTaskExecutor task: bound = false
                ScopedValue bound in the parent, read in a raw Thread.ofVirtual() thread: bound = false
                ScopedValue after run() returned: bound = false
                """);
    }
}

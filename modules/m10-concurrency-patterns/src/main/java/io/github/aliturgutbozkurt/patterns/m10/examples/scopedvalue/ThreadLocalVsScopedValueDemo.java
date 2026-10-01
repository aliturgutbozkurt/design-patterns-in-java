package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue;

import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal.ContextLeaks;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal.InheritanceFacts;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/scopedvalue/ThreadLocalVsScopedValueDemo.java}
 *
 * @see "m10 lesson, section Scoped Values"
 */
public final class ThreadLocalVsScopedValueDemo {

    private ThreadLocalVsScopedValueDemo() {}

    public static void main(String[] args) throws Exception {
        System.out.println("ThreadLocal, 1-thread pool, no remove(): task 2 sees user = "
                + ContextLeaks.secondTaskSeesWithoutRemove() + "   <- leaked from task 1");
        System.out.println("ThreadLocal, 1-thread pool, remove() in finally: task 2 sees user = "
                + ContextLeaks.secondTaskSeesWithFinallyRemove());
        System.out.println("InheritableThreadLocal in a child virtual thread: "
                + InheritanceFacts.inheritableSeenByChild("alice"));
        System.out.println("ThreadLocal in a child virtual thread: " + InheritanceFacts.plainSeenByChild("alice"));
        System.out.println("ScopedValue bound in the parent, read in a newVirtualThreadPerTaskExecutor task: bound = "
                + InheritanceFacts.scopedValueBoundInExecutorTask());
        System.out.println("ScopedValue bound in the parent, read in a raw Thread.ofVirtual() thread: bound = "
                + InheritanceFacts.scopedValueBoundInRawVirtualThread());
        System.out.println("ScopedValue after run() returned: bound = " + InheritanceFacts.scopedValueBoundAfterRun());
    }
}

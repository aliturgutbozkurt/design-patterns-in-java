package io.github.aliturgutbozkurt.patterns.m10.examples.structured;

import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal.InheritanceFacts;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.context.ScopeRules;
import io.github.aliturgutbozkurt.patterns.m10.examples.structured.context.ScopedFanOut;
import java.util.List;

/**
 * Run (preview API, JEP 533): {@code java --enable-preview --source 27 modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/structured/StructuredContextDemo.java}
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public final class StructuredContextDemo {

    private StructuredContextDemo() {}

    public static void main(String[] args) throws Exception {
        System.out.println("subtasks of a StructuredTaskScope: "
                + ScopedFanOut.traceAll("trace-42", List.of("inventory", "pricing", "shipping")));
        System.out.println("a task of newVirtualThreadPerTaskExecutor: bound = "
                + InheritanceFacts.scopedValueBoundInExecutorTask());

        System.out.println("fork from another thread -> " + name(ScopeRules.forkFromAnotherThread()));
        System.out.println("fork after join -> " + name(ScopeRules.forkAfterJoin()));
        System.out.println("Subtask.get() before join -> " + name(ScopeRules.getBeforeJoin()));
        System.out.println("close() after fork without join -> " + name(ScopeRules.closeWithoutJoin()));
    }

    private static String name(RuntimeException e) {
        return e.getClass().getSimpleName();
    }
}

package io.github.aliturgutbozkurt.patterns.m08.examples.state;

import io.github.aliturgutbozkurt.patterns.m08.examples.state.jdk.ThreadStates;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/state/ThreadStateDemo.java}
 *
 * @see "m08 lesson, section State"
 */
public final class ThreadStateDemo {

    private ThreadStateDemo() {}

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Thread.State: " + Arrays.toString(Thread.State.values()));
        System.out.println("platform thread: " + path(ThreadStates.lifecycle(Thread.ofPlatform().daemon(true))));
        System.out.println("virtual thread:  " + path(ThreadStates.lifecycle(Thread.ofVirtual())));
    }

    private static String path(List<Thread.State> states) {
        return states.stream().map(Thread.State::name).collect(Collectors.joining(" -> "));
    }
}

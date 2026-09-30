package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns;

import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.after.Greetings;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before.GreeterFactoryProvider;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before.GreetingStrategy;
import java.util.Arrays;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/antipatterns/PatternitisDemo.java} */
public final class PatternitisDemo {

    private PatternitisDemo() {}

    public static void main(String[] args) {
        GreetingStrategy greeter = GreeterFactoryProvider.defaultFactory().create();
        for (String name : Arrays.asList("Ada", "  Alan ", "")) {
            System.out.println("before: " + greeter.greet(name) + " | after: " + Greetings.greet(name));
        }
        System.out.println("types: before 5 (GreeterFactoryProvider, GreeterFactory, GreetingStrategy, AbstractGreeter,"
                + " FormalGreeter), after 1 (Greetings)");
    }
}

package io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/optional/directory/OptionalDoneRightDemo.java} */
public final class OptionalDoneRightDemo {

    private OptionalDoneRightDemo() {}

    public static void main(String[] args) {
        var directory = CustomerDirectory.sample();

        System.out.println("-- find, then map");
        for (String email : List.of("ada@example.com", "nobody@example.com")) {
            String name = directory.findByEmail(email).map(Customer::name).orElse("(not found)");
            System.out.println(String.format("findByEmail(%s)", email) + " ".repeat(19 - email.length())
                    + "-> " + name);
        }

        System.out.println("-- a chain of lookups: email -> referrer id -> customer -> name");
        for (String email : List.of("grace@example.com", "ada@example.com")) {
            System.out.println(email + " was referred by " + directory.referrerName(email));
        }

        System.out.println("-- orElse vs. orElseGet");
        var calls = new AtomicInteger();
        Optional<String> present = Optional.of("Ada");
        present.orElse(fallback(calls));
        System.out.println("orElse:    fallback computed " + calls.getAndSet(0) + " time(s)");
        present.orElseGet(() -> fallback(calls));
        System.out.println("orElseGet: fallback computed " + calls.get() + " time(s)");

        System.out.println("-- orElseThrow with a message");
        try {
            directory.require(new CustomerId("C-9"));
        } catch (NoSuchElementException e) {
            System.out.println("require(C-9) -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        System.out.println("-- Optional::stream drops the misses");
        System.out.println(directory.namesOf(List.of("linus@example.com", "x@example.com", "ada@example.com")));

        System.out.println("-- no orders is an empty list");
        System.out.println("orders of C-3: " + directory.ordersOf(new CustomerId("C-3")));
        System.out.println("orders of C-1: " + directory.ordersOf(new CustomerId("C-1")));
    }

    private static String fallback(AtomicInteger calls) {
        calls.incrementAndGet();
        return "someone";
    }
}

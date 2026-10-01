package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns;

import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.CheckoutFacade;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.ConfirmationMailer;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.OrderValidator;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after.PricingPolicy;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.before.OrderManager;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/antipatterns/GodClassDemo.java}
 *
 * @see "m11 lesson, section Anti-patterns"
 */
public final class GodClassDemo {

    private GodClassDemo() {}

    public static void main(String[] args) {
        List<CheckoutRequest> requests = List.of(
                new CheckoutRequest("alice", "REGULAR", 3, 3000),
                new CheckoutRequest("bob", "VIP", 10, 1000),
                new CheckoutRequest("carol", "EMPLOYEE", 1, 10000),
                new CheckoutRequest("dave", "GOLD", 1, 500),
                new CheckoutRequest("erin", "VIP", 0, 500));

        var before = new OrderManager();
        var mailer = new ConfirmationMailer();
        List<String> log = new ArrayList<>();
        var after = new CheckoutFacade(OrderValidator.standard(), new OrderRepository(), mailer, log::add);

        for (CheckoutRequest r : requests) {
            String old = before.checkout(r.customer(), r.customerType(), r.quantity(), r.unitPriceCents());
            System.out.println(r.customer() + ": before " + old + " | after " + after.checkout(r));
        }
        System.out.println("same e-mails: " + before.sentEmails().equals(mailer.sent())
                + ", same log: " + before.logLines().equals(log));
        System.out.println("public methods: " + describe(OrderManager.class));
        System.out.println("after the split: " + Stream.of(CheckoutFacade.class, OrderValidator.class,
                PricingPolicy.class, OrderRepository.class, ConfirmationMailer.class)
                .map(GodClassDemo::describe).collect(Collectors.joining(", ")));
    }

    private static String describe(Class<?> type) {
        long count = Arrays.stream(type.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !m.isSynthetic())
                .count();
        return type.getSimpleName() + " " + count;
    }
}

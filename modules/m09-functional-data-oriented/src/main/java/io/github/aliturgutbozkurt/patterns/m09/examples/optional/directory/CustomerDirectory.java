package io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory;

import io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory.Referral.Direct;
import io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory.Referral.ReferredBy;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Customer lookups that use {@code Optional} the way it was designed: as the return type of a query that may find
 * nothing. Parameters are never {@code Optional}, and "no results" is an empty list.
 *
 * @see "m09 lesson, section Optional and Result — Optional as a return type"
 */
public final class CustomerDirectory {

    private final List<Customer> customers;
    private final List<OrderSummary> orders;

    public CustomerDirectory(List<Customer> customers, List<OrderSummary> orders) {
        this.customers = List.copyOf(customers);
        this.orders = List.copyOf(orders);
    }

    /** Three customers (Linus was referred by an id that does not exist) and three orders. */
    public static CustomerDirectory sample() {
        var ada = new CustomerId("C-1");
        return new CustomerDirectory(
                List.of(new Customer(ada, "Ada", "ada@example.com", new Direct()),
                        new Customer(new CustomerId("C-2"), "Grace", "grace@example.com", new ReferredBy(ada)),
                        new Customer(new CustomerId("C-3"), "Linus", "linus@example.com",
                                new ReferredBy(new CustomerId("C-7")))),
                List.of(new OrderSummary("A-1", ada, 45_00),
                        new OrderSummary("A-2", new CustomerId("C-2"), 20_00),
                        new OrderSummary("A-3", ada, 8_00)));
    }

    public Optional<Customer> findByEmail(String email) {
        String key = email.strip().toLowerCase(Locale.ROOT);
        return customers.stream().filter(c -> c.email().equals(key)).findFirst();
    }

    public Optional<Customer> findById(CustomerId id) {
        Objects.requireNonNull(id, "id");
        return customers.stream().filter(c -> c.id().equals(id)).findFirst();
    }

    /** Tries the text as an e-mail first, then as a customer id ({@code or}). */
    public Optional<Customer> findByEmailOrId(String text) {
        return findByEmail(text)
                .or(() -> Optional.of(text).filter(t -> t.startsWith("C-")).map(CustomerId::new)
                        .flatMap(this::findById));
    }

    /** For callers that know the customer must exist: a missing one is a bug, reported with a clear message. */
    public Customer require(CustomerId id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("no customer with id " + id));
    }

    /** The name of whoever referred the customer with this e-mail, or {@code "nobody"}. */
    public String referrerName(String email) {
        return findByEmail(email)
                .flatMap(Customer::referrer)
                .flatMap(this::findById)
                .map(Customer::name)
                .orElse("nobody");
    }

    /** Names of the customers that exist, in the order of {@code emails}; misses are dropped. */
    public List<String> namesOf(List<String> emails) {
        return emails.stream()
                .map(this::findByEmail)
                .flatMap(Optional::stream)
                .map(Customer::name)
                .toList();
    }

    /** Never {@code Optional<List<…>>}: an unknown customer simply has no orders. */
    public List<OrderSummary> ordersOf(CustomerId id) {
        return orders.stream().filter(o -> o.customer().equals(id)).toList();
    }
}

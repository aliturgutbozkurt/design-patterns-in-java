package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.before;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ANTI-PATTERN — god class: validation, pricing by type code, persistence, e-mail text and logging in one place.
 * Every change to any of them touches this class, and none of them can be tested or reused alone.
 *
 * @see "m11 lesson, section Anti-patterns — god class"
 */
public final class OrderManager {

    private final Map<String, String> orders = new HashMap<>();
    private final List<String> emails = new ArrayList<>();
    private final List<String> logLines = new ArrayList<>();
    private int counter;

    /** Validates, prices, saves, e-mails and logs; returns {@code OK <id> <total>} or {@code REJECTED <reason>}. */
    public String checkout(String customer, String customerType, int quantity, long unitPriceCents) {
        String problem = validate(customer, customerType, quantity, unitPriceCents);
        if (problem != null) {
            log("rejected " + customer + ": " + problem);
            return "REJECTED " + problem;
        }
        long total = calculateTotal(customerType, quantity, unitPriceCents);
        String id = save(customer, quantity, total);
        sendEmail(composeEmail(customer, id, quantity, total));
        log("checkout " + customer + " " + id + " " + format(total));
        return "OK " + id + " " + format(total);
    }

    public String validate(String customer, String customerType, int quantity, long unitPriceCents) {
        if (customer == null || customer.isBlank()) {
            return "missing customer";
        } else if (quantity <= 0) {
            return "invalid quantity";
        } else if (unitPriceCents <= 0) {
            return "invalid price";
        } else if (discountPercent(customerType) < 0) {
            return "unknown customer type: " + customerType;
        }
        return null;
    }

    public long calculateTotal(String customerType, int quantity, long unitPriceCents) {
        long total = quantity * unitPriceCents * (100 - discountPercent(customerType)) / 100;
        if (quantity >= 10) {
            total = total * 95 / 100; // bulk discount
        }
        return total;
    }

    public int discountPercent(String customerType) {
        if ("REGULAR".equals(customerType)) {
            return 0;
        } else if ("VIP".equals(customerType)) {
            return 10;
        } else if ("EMPLOYEE".equals(customerType)) {
            return 30;
        } else {
            return -1;
        }
    }

    public String save(String customer, int quantity, long totalCents) {
        String id = "order-" + ++counter;
        orders.put(id, customer + ";" + quantity + ";" + totalCents);
        return id;
    }

    public String findOrder(String id) {
        return orders.get(id);
    }

    public int orderCount() {
        return orders.size();
    }

    public String composeEmail(String customer, String id, int quantity, long totalCents) {
        return "To: " + customer + " | Your order " + id + " (" + quantity + " items) is confirmed. Total: "
                + format(totalCents);
    }

    public void sendEmail(String text) {
        emails.add(text);
    }

    public List<String> sentEmails() {
        return List.copyOf(emails);
    }

    public void log(String line) {
        logLines.add(line);
    }

    public List<String> logLines() {
        return List.copyOf(logLines);
    }

    private static String format(long cents) {
        return BigDecimal.valueOf(cents, 2).toPlainString();
    }
}

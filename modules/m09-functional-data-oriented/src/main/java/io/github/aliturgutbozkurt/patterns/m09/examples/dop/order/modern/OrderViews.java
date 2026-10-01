package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern;

import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Cancelled;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Draft;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Paid;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Placed;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Shipped;
import java.util.Locale;

/**
 * Operations over orders as functions: an exhaustive {@code switch} with record patterns and no {@code default}, so
 * adding a sixth state makes this file fail to compile until it handles it.
 *
 * @see "m09 lesson, section Data-oriented programming — Modern Java 27"
 */
public final class OrderViews {

    private OrderViews() {}

    public static String describe(Order order) {
        String text = switch (order) {
            case Draft(_, var lines) -> "draft, " + lines.size() + " line(s), total " + money(totalCents(order));
            case Placed _ -> "placed, awaiting payment of " + money(totalCents(order));
            case Paid(_, _, var paymentRef) -> "paid (" + paymentRef + ")";
            case Shipped(_, _, _, var trackingCode) -> "shipped, tracking " + trackingCode;
            case Cancelled(_, _, var reason, var refundDue) ->
                    "cancelled (" + reason + "), " + (refundDue ? "refund due" : "nothing to refund");
        };
        return order.id() + ": " + text;
    }

    /** The same in every state: the lines travel with the order. */
    public static long totalCents(Order order) {
        return order.lines().stream().mapToLong(OrderLine::totalCents).sum();
    }

    private static String money(long cents) {
        return String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100);
    }
}

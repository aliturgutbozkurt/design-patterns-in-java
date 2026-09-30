package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.classic;

/**
 * The "before" operations: every method re-checks the status string at run time, and {@link #describe} needs a
 * fallback branch because the compiler cannot know which statuses exist.
 *
 * @see "m09 lesson, section Data-oriented programming — Classic Java"
 */
public class LegacyOrderService {

    public void place(MutableOrder order) {
        requireStatus(order, "DRAFT", "place");
        order.setStatus("PLACED");
    }

    public void pay(MutableOrder order, String paymentRef) {
        requireStatus(order, "PLACED", "pay");
        order.setPaymentRef(paymentRef);
        order.setStatus("PAID");
    }

    public void ship(MutableOrder order, String trackingCode) {
        requireStatus(order, "PAID", "ship");
        order.setTrackingCode(trackingCode);
        order.setStatus("SHIPPED");
    }

    public String describe(MutableOrder order) {
        String status = order.getStatus();
        String text;
        if (status.equals("DRAFT")) {
            text = "draft";
        } else if (status.equals("PLACED")) {
            text = "placed, awaiting payment";
        } else if (status.equals("PAID")) {
            text = "paid (" + order.getPaymentRef() + ")";
        } else if (status.equals("SHIPPED")) {
            text = "shipped, tracking " + order.getTrackingCode();
        } else if (status.equals("CANCELLED")) {
            text = "cancelled (" + order.getCancelReason() + ")";
        } else {
            text = "unknown";
        }
        return order.getId() + ": " + text;
    }

    private static void requireStatus(MutableOrder order, String expected, String action) {
        if (!order.getStatus().equals(expected)) {
            throw new IllegalStateException(
                    "cannot " + action + " " + order.getId() + " in status " + order.getStatus());
        }
    }
}

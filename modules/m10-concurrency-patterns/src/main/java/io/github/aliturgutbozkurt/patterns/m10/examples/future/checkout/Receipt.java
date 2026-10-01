package io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout;

/**
 * The outcome of a checkout. A failure anywhere in the pipeline becomes a {@link Status#DECLINED} receipt whose
 * detail is the message of the original exception.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
public record Receipt(String customer, Status status, long totalCents, String detail) {

    /** Whether the order went through. */
    public enum Status { APPROVED, DECLINED }

    public static Receipt approved(String customer, long totalCents, String paymentId) {
        return new Receipt(customer, Status.APPROVED, totalCents, "payment " + paymentId);
    }

    public static Receipt declined(String customer, String reason) {
        return new Receipt(customer, Status.DECLINED, 0, reason);
    }
}

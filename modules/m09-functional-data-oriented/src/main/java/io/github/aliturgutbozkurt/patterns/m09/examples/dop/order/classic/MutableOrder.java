package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.classic;

/**
 * The "before" model: one mutable class for every state, a {@code String} status and fields that are only
 * meaningful in some states (and {@code null} in all the others). Nothing stops a caller from building nonsense.
 *
 * @see "m09 lesson, section Data-oriented programming — Classic Java"
 */
public class MutableOrder {

    private final String id;
    private final long totalCents;
    private String status = "DRAFT";
    private String paymentRef;
    private String trackingCode;
    private String cancelReason;

    public MutableOrder(String id, long totalCents) {
        this.id = id;
        this.totalCents = totalCents;
    }

    public String getId() {
        return id;
    }

    public long getTotalCents() {
        return totalCents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}

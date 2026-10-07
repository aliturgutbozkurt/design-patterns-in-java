package io.github.aliturgutbozkurt.patterns.capstone.api.external;

/**
 * GIVEN — do not modify. The warehouse's blocking API; every call may take time and may throw
 * {@link WarehouseException}. {@code orderRef} is the order id ({@code order-1}).
 *
 * @see "capstone brief, Business rules — Fulfilment (F9)"
 */
public interface WarehouseApi {

    /** Picks {@code quantity} units of {@code sku} for the order. */
    void pick(String orderRef, String sku, int quantity);

    /** Packs the order's picked items and returns the parcel id. */
    String pack(String orderRef);

    /** Ships a parcel to a postal code and returns the tracking code. */
    String ship(String parcelId, String postalCode);
}

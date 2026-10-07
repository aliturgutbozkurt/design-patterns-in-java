package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.WarehouseException;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scriptable {@link WarehouseApi} for deterministic concurrency tests. Records every call with the calling thread's
 * kind, tracks how many orders are in flight (from their first call until {@code ship} returns or a call fails), can
 * hold calls on a latch or a barrier and can fail a chosen order. Parcels are {@code PCL-<n>} and tracking codes
 * {@code TRK-0001}, … by order number. Every wait gives up after {@link #SAFETY_TIMEOUT} with a
 * {@link WarehouseException}, so a broken implementation fails instead of hanging. Thread-safe.
 */
public final class ScriptedWarehouse implements WarehouseApi {

    /** The longest any scripted wait blocks. */
    public static final Duration SAFETY_TIMEOUT = Duration.ofSeconds(5);

    /**
     * One call.
     *
     * @param operation     {@code pick}, {@code pack} or {@code ship}
     * @param orderRef      the order the call belongs to
     * @param arguments     pick: sku and quantity; pack: none; ship: parcel id and postal code
     * @param virtualThread whether the calling thread was virtual
     */
    public record Call(String operation, String orderRef, List<String> arguments, boolean virtualThread) {
    }

    private final List<Call> calls = new CopyOnWriteArrayList<>();
    private final Set<String> inFlight = ConcurrentHashMap.newKeySet();
    private final AtomicInteger peak = new AtomicInteger();
    private final Map<String, String> failures = new ConcurrentHashMap<>();
    private final Map<String, CountDownLatch> holds = new ConcurrentHashMap<>();
    private final Map<String, CountDownLatch> shipped = new ConcurrentHashMap<>();
    private volatile CyclicBarrier pickBarrier;

    /** Every {@code pick} call waits at {@code barrier} before it returns. */
    public void holdPicksAt(CyclicBarrier barrier) {
        pickBarrier = Objects.requireNonNull(barrier, "barrier");
    }

    /** The picks of {@code orderRef} wait until {@code release} reaches zero. */
    public void holdOrderUntil(String orderRef, CountDownLatch release) {
        holds.put(orderRef, Objects.requireNonNull(release, "release"));
    }

    /** A latch that opens once {@code ship} for {@code orderRef} has completed. */
    public CountDownLatch shippedSignal(String orderRef) {
        return shipped.computeIfAbsent(orderRef, _ -> new CountDownLatch(1));
    }

    /** Every {@code pick} of {@code orderRef} throws a {@link WarehouseException} with {@code message}. */
    public void failOrder(String orderRef, String message) {
        failures.put(orderRef, message);
    }

    @Override
    public void pick(String orderRef, String sku, int quantity) {
        record("pick", orderRef, List.of(sku, String.valueOf(quantity)));
        try {
            String failure = failures.get(orderRef);
            if (failure != null) {
                throw new WarehouseException(failure);
            }
            CountDownLatch hold = holds.get(orderRef);
            if (hold != null) {
                await(hold);
            }
            CyclicBarrier barrier = pickBarrier;
            if (barrier != null) {
                await(barrier);
            }
        } catch (RuntimeException e) {
            inFlight.remove(orderRef);
            throw e;
        }
    }

    @Override
    public String pack(String orderRef) {
        record("pack", orderRef, List.of());
        return "PCL-" + new OrderId(orderRef).number();
    }

    @Override
    public String ship(String parcelId, String postalCode) {
        String orderRef = "order-" + parcelId.substring("PCL-".length());
        record("ship", orderRef, List.of(parcelId, postalCode));
        inFlight.remove(orderRef);
        shippedSignal(orderRef).countDown();
        return String.format(Locale.ROOT, "TRK-%04d", new OrderId(orderRef).number());
    }

    /** Every call so far, in order. */
    public List<Call> calls() {
        return List.copyOf(calls);
    }

    /** The calls that belong to one order, in order. */
    public List<Call> callsFor(String orderRef) {
        return calls.stream().filter(call -> call.orderRef().equals(orderRef)).toList();
    }

    /** The highest number of orders that were in flight at the same time. */
    public int peakOrdersInFlight() {
        return peak.get();
    }

    private void record(String operation, String orderRef, List<String> arguments) {
        calls.add(new Call(operation, orderRef, arguments, Thread.currentThread().isVirtual()));
        inFlight.add(orderRef);
        peak.accumulateAndGet(inFlight.size(), Math::max);
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(SAFETY_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
                throw new WarehouseException("test hold not released within " + SAFETY_TIMEOUT);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WarehouseException("interrupted", e);
        }
    }

    private static void await(CyclicBarrier barrier) {
        try {
            barrier.await(SAFETY_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WarehouseException("interrupted", e);
        } catch (BrokenBarrierException | TimeoutException e) {
            throw new WarehouseException("orders did not meet at the barrier: " + e, e);
        }
    }
}

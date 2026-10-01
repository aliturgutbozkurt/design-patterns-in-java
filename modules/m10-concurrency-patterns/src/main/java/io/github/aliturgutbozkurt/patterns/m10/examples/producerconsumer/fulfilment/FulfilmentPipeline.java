package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A multi-stage Producer–Consumer: {@code pick → pack → ship}. Each stage runs on its own thread, consumes from one
 * bounded queue and produces into the next. A stalled stage fills its input queue, then the queues before it, until
 * the producer feels the back-pressure. Shutdown travels stage by stage: each stage forwards the poison pill only
 * after it has processed every order in front of it.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public final class FulfilmentPipeline {

    /** The stage names, in the order every order passes them. */
    public static final List<String> STAGES = List.of("pick", "pack", "ship");

    @FunctionalInterface
    private interface Downstream {
        void accept(Parcel parcel) throws InterruptedException;
    }

    private final BlockingQueue<Parcel> intake;
    private final ReentrantLock intakeLock = new ReentrantLock();   // makes "accepting?" + put one atomic step
    private boolean accepting = true;                               // guarded by intakeLock
    private boolean pillSent;                                       // guarded by intakeLock
    private final List<Thread> stageThreads = new ArrayList<>();
    private final Queue<Shipment> shipments = new ConcurrentLinkedQueue<>();
    private final Queue<String> shutdownLog = new ConcurrentLinkedQueue<>();

    /** Starts one thread per stage from {@code threadFactory}; every queue holds {@code queueCapacity} orders. */
    public FulfilmentPipeline(int queueCapacity, Stage pick, Stage pack, Stage ship, ThreadFactory threadFactory) {
        List<Stage> stages = List.of(pick, pack, ship);
        List<BlockingQueue<Parcel>> inputs = new ArrayList<>();
        for (int i = 0; i < stages.size(); i++) {
            inputs.add(new ArrayBlockingQueue<>(queueCapacity));
        }
        this.intake = inputs.getFirst();
        for (int i = 0; i < stages.size(); i++) {
            String name = STAGES.get(i);
            Stage stage = stages.get(i);
            BlockingQueue<Parcel> in = inputs.get(i);
            Downstream out = i + 1 < stages.size() ? inputs.get(i + 1)::put : this::deliver;
            Thread thread = threadFactory.newThread(() -> runStage(name, stage, in, out));
            stageThreads.add(thread);
        }
        stageThreads.forEach(Thread::start);
    }

    /** At most this many orders fit into a pipeline whose queues hold {@code queueCapacity} each. */
    public static int maxInFlight(int queueCapacity) {
        return STAGES.size() * (queueCapacity + 1);      // every queue full + every stage holding one order
    }

    /** Hands an order to the first stage, blocking while its queue is full. */
    public void submit(Order order) throws InterruptedException {
        Objects.requireNonNull(order, "order");
        intakeLock.lockInterruptibly();
        try {
            ensureAccepting();
            intake.put(new Parcel.InTransit(order, List.of()));
        } finally {
            intakeLock.unlock();
        }
    }

    /** Like {@link #submit}, but gives up after {@code timeout}: {@code false} means the pipeline is full. */
    public boolean trySubmit(Order order, Duration timeout) throws InterruptedException {
        Objects.requireNonNull(order, "order");
        intakeLock.lockInterruptibly();
        try {
            ensureAccepting();
            return intake.offer(new Parcel.InTransit(order, List.of()), timeout.toNanos(), TimeUnit.NANOSECONDS);
        } finally {
            intakeLock.unlock();
        }
    }

    /**
     * Stops accepting orders, sends the poison pill after the last order, and waits up to {@code timeout} for
     * every stage thread to end. Returns {@code false} if some stage is still running when the time is up.
     */
    public boolean shutdownAndAwait(Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        intakeLock.lockInterruptibly();
        try {
            accepting = false;
            if (!pillSent) {                                // the pill queues up behind the last order
                pillSent = intake.offer(new Parcel.EndOfOrders(), timeout.toNanos(), TimeUnit.NANOSECONDS);
            }
            if (!pillSent) {
                return false;                               // intake still full: try again later
            }
        } finally {
            intakeLock.unlock();
        }
        for (Thread thread : stageThreads) {
            long left = deadline - System.nanoTime();
            if (left <= 0 || !thread.join(Duration.ofNanos(left))) {
                return false;
            }
        }
        return true;
    }

    /** The shipped orders so far, sorted by order id (not by the order in which they happened to finish). */
    public List<Shipment> shipments() {
        return shipments.stream().sorted(Comparator.comparingLong(Shipment::orderId)).toList();
    }

    /** One entry per stage that received the pill, in the order they stopped. */
    public List<String> shutdownLog() {
        return List.copyOf(shutdownLog);
    }

    private void ensureAccepting() {
        if (!accepting) {
            throw new IllegalStateException("pipeline is shut down");
        }
    }

    private void runStage(String name, Stage stage, BlockingQueue<Parcel> in, Downstream out) {
        try {
            while (true) {
                switch (in.take()) {
                    case Parcel.InTransit(Order order, List<String> log) -> {
                        stage.process(order);
                        List<String> next = new ArrayList<>(log);
                        next.add(name);
                        out.accept(new Parcel.InTransit(order, next));
                    }
                    case Parcel.EndOfOrders pill -> {
                        shutdownLog.add(name + " drained");  // everything before the pill is done
                        out.accept(pill);                    // forward exactly one pill downstream
                        return;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();              // let the stage thread end, flag preserved
        }
    }

    private void deliver(Parcel parcel) {
        if (parcel instanceof Parcel.InTransit(Order order, List<String> log)) {
            shipments.add(new Shipment(order.id(), log));
        }
    }
}

package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Spy error sink ({@code ShopEnvironment.errors()}): keeps every reported exception. Thread-safe. */
public final class RecordingErrors implements Consumer<Throwable> {

    private final List<Throwable> errors = new CopyOnWriteArrayList<>();

    @Override
    public void accept(Throwable error) {
        errors.add(error);
    }

    /** Every reported exception so far. */
    public List<Throwable> all() {
        return List.copyOf(errors);
    }
}

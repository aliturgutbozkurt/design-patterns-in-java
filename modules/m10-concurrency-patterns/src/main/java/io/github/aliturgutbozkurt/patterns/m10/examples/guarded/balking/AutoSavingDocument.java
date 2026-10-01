package io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Balking: {@link #save()} returns at once when its precondition fails, because nothing changed or a save is
 * already running. Waiting would be pointless: the running save or the next auto-save tick does the work. A
 * version number tells "dirty" from "clean", so an edit made during a save keeps the document dirty.
 *
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
public final class AutoSavingDocument {

    private record Snapshot(long version, String text) {}

    private final DocumentStore store;
    private final AtomicReference<Snapshot> current = new AtomicReference<>(new Snapshot(0, ""));
    private final AtomicLong savedVersion = new AtomicLong(0);
    private final AtomicBoolean saving = new AtomicBoolean(false);

    public AutoSavingDocument(DocumentStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    /** Replaces the text; every edit is a new version. */
    public void edit(String text) {
        Objects.requireNonNull(text, "text");
        current.updateAndGet(old -> new Snapshot(old.version() + 1, text));
    }

    public String text() {
        return current.get().text();
    }

    /** {@code true} while the latest edit has not been written yet. */
    public boolean isDirty() {
        return current.get().version() != savedVersion.get();
    }

    /** Writes the latest version, or balks. Never blocks waiting for another save. */
    public SaveResult save() {
        if (!isDirty()) {
            return SaveResult.SKIPPED_CLEAN;                    // balk: nothing to do
        }
        if (!saving.compareAndSet(false, true)) {
            return SaveResult.SKIPPED_IN_PROGRESS;              // balk: someone else is saving right now
        }
        try {
            Snapshot snapshot = current.get();
            if (snapshot.version() == savedVersion.get()) {
                return SaveResult.SKIPPED_CLEAN;                // a save finished between the two checks
            }
            store.write(snapshot.version(), snapshot.text());
            savedVersion.set(snapshot.version());               // an edit made meanwhile has a higher version
            return SaveResult.SAVED;
        } finally {
            saving.set(false);
        }
    }
}

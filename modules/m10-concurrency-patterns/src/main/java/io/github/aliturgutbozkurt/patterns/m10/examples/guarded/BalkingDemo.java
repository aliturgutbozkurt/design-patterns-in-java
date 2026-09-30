package io.github.aliturgutbozkurt.patterns.m10.examples.guarded;

import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking.AutoSavingDocument;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking.DocumentStore;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking.SaveResult;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/guarded/BalkingDemo.java}
 *
 * <p>A slow store is simulated with latches, so "save while another save is writing" happens every time.
 */
public final class BalkingDemo {

    private BalkingDemo() {}

    public static void main(String[] args) throws Exception {
        List<String> written = new CopyOnWriteArrayList<>();
        var slowWriteStarted = new CountDownLatch(1);
        var diskCatchesUp = new CountDownLatch(1);
        DocumentStore store = (version, text) -> {
            if (version == 2) {                         // the second version hits a slow disk
                slowWriteStarted.countDown();
                awaitUninterruptibly(diskCatchesUp);
            }
            written.add("v" + version + ": " + text);
        };
        var document = new AutoSavingDocument(store);

        System.out.println("save() on a new document -> " + document.save());
        document.edit("Dear team,");
        System.out.println("edit, save() -> " + document.save());
        System.out.println("save() again -> " + document.save());

        document.edit("Dear team, the release is on Friday.");
        var autoSave = new CompletableFuture<SaveResult>();
        Thread.ofVirtual().start(() -> autoSave.complete(document.save()));
        slowWriteStarted.await();
        System.out.println("edit; auto-save #1 is now writing (the store is slow)");
        System.out.println("save() while #1 writes -> " + document.save());
        document.edit("Dear team, the release is on Friday. Thanks!");
        System.out.println("edit while #1 writes");

        diskCatchesUp.countDown();
        System.out.println("auto-save #1 -> " + autoSave.get() + ", still dirty: " + document.isDirty());
        System.out.println("save() -> " + document.save());
        System.out.println("store received: " + written);
    }

    private static void awaitUninterruptibly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while writing", e);
        }
    }
}

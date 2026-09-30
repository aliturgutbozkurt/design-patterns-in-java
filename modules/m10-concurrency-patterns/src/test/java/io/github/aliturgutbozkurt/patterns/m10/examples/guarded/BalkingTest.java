package io.github.aliturgutbozkurt.patterns.m10.examples.guarded;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking.AutoSavingDocument;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking.DocumentStore;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking.SaveResult;
import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class BalkingTest {

    /** Records every write. */
    private static final class RecordingStore implements DocumentStore {
        final List<String> writes = new CopyOnWriteArrayList<>();

        @Override
        public void write(long version, String text) {
            writes.add("v" + version + ": " + text);
        }
    }

    /** A store whose write signals {@code entered} and then waits until the test opens {@code release}. */
    private static final class GatedStore implements DocumentStore {
        final RecordingStore delegate = new RecordingStore();
        final CountDownLatch entered = new CountDownLatch(1);
        final CountDownLatch release = new CountDownLatch(1);

        @Override
        public void write(long version, String text) {
            entered.countDown();
            try {
                Await.latch(release);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            delegate.write(version, text);
        }
    }

    private static CompletableFuture<SaveResult> saveInBackground(AutoSavingDocument document) {
        var result = new CompletableFuture<SaveResult>();
        Thread.ofVirtual().start(() -> {
            try {
                result.complete(document.save());
            } catch (RuntimeException e) {
                result.completeExceptionally(e);
            }
        });
        return result;
    }

    @Test
    void cleanDocumentBalksWithSkippedCleanAndDoesNotCallTheStore() {
        var store = new RecordingStore();
        var document = new AutoSavingDocument(store);
        assertThat(document.save()).isEqualTo(SaveResult.SKIPPED_CLEAN);
        assertThat(store.writes).isEmpty();
    }

    @Test
    void editedDocumentIsSavedOnceAndThenIsClean() {
        var store = new RecordingStore();
        var document = new AutoSavingDocument(store);
        document.edit("hello");
        assertThat(document.isDirty()).isTrue();
        assertThat(document.save()).isEqualTo(SaveResult.SAVED);
        assertThat(document.isDirty()).isFalse();
        assertThat(document.save()).isEqualTo(SaveResult.SKIPPED_CLEAN);
        assertThat(store.writes).containsExactly("v1: hello");
    }

    @Test
    void saveWhileAnotherSaveIsInsideTheStoreBalksWithSkippedInProgress() throws Exception {
        var store = new GatedStore();
        var document = new AutoSavingDocument(store);
        document.edit("draft");
        var first = saveInBackground(document);
        Await.latch(store.entered);                     // the first save is now inside write()

        assertThat(document.save()).isEqualTo(SaveResult.SKIPPED_IN_PROGRESS);   // returns at once

        store.release.countDown();
        assertThat(first.get(5, TimeUnit.SECONDS)).isEqualTo(SaveResult.SAVED);
        assertThat(store.delegate.writes).containsExactly("v1: draft");
    }

    @Test
    void editDuringASaveLeavesTheDocumentDirtySoTheNextSaveWritesAgain() throws Exception {
        var store = new GatedStore();
        var document = new AutoSavingDocument(store);
        document.edit("draft");
        var first = saveInBackground(document);
        Await.latch(store.entered);
        document.edit("draft, edited");

        store.release.countDown();
        assertThat(first.get(5, TimeUnit.SECONDS)).isEqualTo(SaveResult.SAVED);
        assertThat(document.isDirty()).isTrue();
        assertThat(document.save()).isEqualTo(SaveResult.SAVED);
        assertThat(store.delegate.writes).containsExactly("v1: draft", "v2: draft, edited");
    }

    @Test
    void hundredConcurrentSavesOfOneDirtyDocumentWriteExactlyOnce() throws Exception {
        var store = new RecordingStore();
        var document = new AutoSavingDocument(store);
        document.edit("final");
        var start = new CountDownLatch(1);
        List<Future<SaveResult>> results = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; i++) {
                results.add(executor.submit(() -> {
                    Await.latch(start);
                    return document.save();
                }));
            }
            start.countDown();                          // release all 100 at once
        }
        List<SaveResult> outcomes = new ArrayList<>();
        for (Future<SaveResult> result : results) {
            outcomes.add(result.get(5, TimeUnit.SECONDS));
        }
        assertThat(outcomes).filteredOn(SaveResult.SAVED::equals).hasSize(1);
        assertThat(outcomes).doesNotContainNull();
        assertThat(store.writes).containsExactly("v1: final");
    }

    @Test
    void failingStoreLeavesTheDocumentDirtyAndReleasesTheSaveFlag() {
        var failure = new IllegalStateException("disk full");
        var document = new AutoSavingDocument((_, _) -> {
            throw failure;
        });
        document.edit("text");
        assertThatThrownBy(document::save).isSameAs(failure);
        assertThat(document.isDirty()).isTrue();
        assertThatThrownBy(document::save).isSameAs(failure);    // not SKIPPED_IN_PROGRESS: the flag was reset
    }

    @Test
    void demoPrintsEveryBalkingDecision() {
        assertThat(Demos.output(() -> BalkingDemo.main(new String[0]))).isEqualTo("""
                save() on a new document -> SKIPPED_CLEAN
                edit, save() -> SAVED
                save() again -> SKIPPED_CLEAN
                edit; auto-save #1 is now writing (the store is slow)
                save() while #1 writes -> SKIPPED_IN_PROGRESS
                edit while #1 writes
                auto-save #1 -> SAVED, still dirty: true
                save() -> SAVED
                store received: [v1: Dear team,, v2: Dear team, the release is on Friday., \
                v3: Dear team, the release is on Friday. Thanks!]
                """);
    }
}

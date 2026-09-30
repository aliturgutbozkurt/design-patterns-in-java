package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.protection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProtectionProxyTest {

    /** Records every call that reaches the real store. */
    private static final class SpyStore implements DocumentStore {
        final List<String> calls = new ArrayList<>();
        private final DocumentStore real = new InMemoryDocumentStore();

        @Override
        public String read(String id) {
            calls.add("read " + id);
            return real.read(id);
        }

        @Override
        public void write(String id, String content) {
            calls.add("write " + id);
            real.write(id, content);
        }

        @Override
        public void delete(String id) {
            calls.add("delete " + id);
            real.delete(id);
        }
    }

    private static DocumentStore storeFor(SpyStore spy, Role role) {
        spy.real.write("doc", "text");
        return new ProtectedDocumentStore(spy, new User("zeynep", role));
    }

    @Test
    void viewerMayOnlyRead() {
        var spy = new SpyStore();
        DocumentStore store = storeFor(spy, Role.VIEWER);
        assertThat(store.read("doc")).isEqualTo("text");
        assertThatThrownBy(() -> store.write("doc", "x")).isInstanceOf(PermissionDeniedException.class);
        assertThatThrownBy(() -> store.delete("doc")).isInstanceOf(PermissionDeniedException.class);
    }

    @Test
    void editorMayReadAndWrite() {
        var spy = new SpyStore();
        DocumentStore store = storeFor(spy, Role.EDITOR);
        store.write("doc", "new text");
        assertThat(store.read("doc")).isEqualTo("new text");
        assertThatThrownBy(() -> store.delete("doc")).isInstanceOf(PermissionDeniedException.class);
    }

    @Test
    void adminMayAlsoDelete() {
        var spy = new SpyStore();
        DocumentStore store = storeFor(spy, Role.ADMIN);
        store.write("doc", "x");
        store.delete("doc");
        assertThat(spy.calls).containsExactly("write doc", "delete doc");
    }

    @Test
    void deniedCallNamesUserAndOperationAndNeverReachesTheRealStore() {
        var spy = new SpyStore();
        DocumentStore store = storeFor(spy, Role.VIEWER);
        assertThatThrownBy(() -> store.delete("doc"))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessage("zeynep (VIEWER) may not DELETE doc");
        assertThatThrownBy(() -> store.write("doc", "x")).hasMessage("zeynep (VIEWER) may not WRITE doc");
        assertThat(spy.calls).isEmpty();
    }

    @Test
    void permissionRuleCoversEveryRoleAndOperation() {
        for (Operation operation : Operation.values()) {
            assertThat(ProtectedDocumentStore.allowed(Role.ADMIN, operation)).isTrue();
        }
        assertThat(ProtectedDocumentStore.allowed(Role.VIEWER, Operation.READ)).isTrue();
        assertThat(ProtectedDocumentStore.allowed(Role.VIEWER, Operation.WRITE)).isFalse();
        assertThat(ProtectedDocumentStore.allowed(Role.EDITOR, Operation.WRITE)).isTrue();
        assertThat(ProtectedDocumentStore.allowed(Role.EDITOR, Operation.DELETE)).isFalse();
    }

    @Test
    void proxyIsUsableWhereverADocumentStoreIsExpected() {
        DocumentStore store = new ProtectedDocumentStore(new InMemoryDocumentStore(), new User("ali", Role.EDITOR));
        assertThat(copy(store, "a", "b")).isEqualTo("hello");
    }

    private static String copy(DocumentStore store, String from, String to) {    // client code: knows no proxy
        store.write(from, "hello");
        store.write(to, store.read(from));
        return store.read(to);
    }

    @Test
    void rejectsMissingParts() {
        assertThatNullPointerException().isThrownBy(() -> new ProtectedDocumentStore(null, new User("a", Role.ADMIN)));
        assertThatNullPointerException().isThrownBy(() -> new ProtectedDocumentStore(new InMemoryDocumentStore(), null));
        assertThatNullPointerException().isThrownBy(() -> new User("a", null));
    }

    @Test
    void demoPrintsWhatEachRoleMayDo() {
        assertThat(Console.capture(() -> ProtectionProxyDemo.main(new String[0]))).isEqualTo("""
                deniz (VIEWER):
                  read -> draft
                  denied: deniz (VIEWER) may not WRITE contract-7
                  denied: deniz (VIEWER) may not DELETE draft-1
                ece (EDITOR):
                  read -> draft
                  write -> ok
                  denied: ece (EDITOR) may not DELETE draft-1
                mert (ADMIN):
                  read -> revised by ece
                  write -> ok
                  delete -> ok
                """);
    }
}

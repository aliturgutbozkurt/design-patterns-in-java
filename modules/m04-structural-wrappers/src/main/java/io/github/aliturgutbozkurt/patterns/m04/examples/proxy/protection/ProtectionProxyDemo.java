package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.protection;

import java.util.List;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/proxy/protection/ProtectionProxyDemo.java} */
public final class ProtectionProxyDemo {

    private ProtectionProxyDemo() {}

    public static void main(String[] args) {
        DocumentStore real = new InMemoryDocumentStore();
        real.write("contract-7", "draft");

        for (User user : List.of(
                new User("deniz", Role.VIEWER), new User("ece", Role.EDITOR), new User("mert", Role.ADMIN))) {
            DocumentStore store = new ProtectedDocumentStore(real, user);    // same interface as the real store
            System.out.println(user.name() + " (" + user.role() + "):");
            attempt(() -> System.out.println("  read -> " + store.read("contract-7")));
            attempt(() -> {
                store.write("contract-7", "revised by " + user.name());
                System.out.println("  write -> ok");
            });
            attempt(() -> {
                store.delete("draft-1");
                System.out.println("  delete -> ok");
            });
        }
    }

    private static void attempt(Runnable action) {
        try {
            action.run();
        } catch (PermissionDeniedException e) {
            System.out.println("  denied: " + e.getMessage());
        }
    }
}

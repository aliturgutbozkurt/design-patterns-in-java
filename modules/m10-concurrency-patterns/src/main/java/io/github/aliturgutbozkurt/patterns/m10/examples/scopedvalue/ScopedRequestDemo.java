package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue;

import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.AuditLog;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.OrderService;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.Principal;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.RequestContext;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.RequestHandler;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/scopedvalue/ScopedRequestDemo.java}
 *
 * <p>Three requests run on three virtual threads at the same time; the audit log is printed sorted by request id.
 */
public final class ScopedRequestDemo {

    private ScopedRequestDemo() {}

    public static void main(String[] args) {
        var audit = new AuditLog();
        var handler = new RequestHandler(new OrderService(audit));
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> handler.placeOrder("req-1", new Principal("alice", Set.of("customer")), "book"));
            executor.submit(() -> handler.placeOrder("req-2", new Principal("bob", Set.of("customer")), "mug"));
            executor.submit(() -> handler.placeOrder("req-3", new Principal("carol", Set.of("customer")), "pen"));
        }
        for (AuditLog.Entry entry : audit.entries()) {
            System.out.println(String.format(Locale.ROOT, "%s | %-6s | %s",
                    entry.requestId(), entry.principal(), entry.action()));
        }

        System.out.println("after the requests: PRINCIPAL bound? " + RequestContext.PRINCIPAL.isBound());
        try {
            RequestContext.PRINCIPAL.get();
        } catch (NoSuchElementException e) {
            System.out.println("PRINCIPAL.get() outside a request -> NoSuchElementException");
        }
        System.out.println("PRINCIPAL.orElse(ANONYMOUS) -> "
                + RequestContext.PRINCIPAL.orElse(Principal.ANONYMOUS).name());
    }
}

package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.AuditLog;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.OrderService;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.Principal;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.RequestContext;
import io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request.RequestHandler;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class ScopedRequestTest {

    private static final Principal ALICE = new Principal("alice", Set.of("customer"));

    private final AuditLog audit = new AuditLog();
    private final RequestHandler handler = new RequestHandler(new OrderService(audit));

    @Test
    void insideTheHandlerTheBoundPrincipalAndRequestIdAreVisible() {
        assertThat(handler.handle("r-1", ALICE, RequestContext.PRINCIPAL::get)).isEqualTo(ALICE);
        assertThat(handler.handle("r-1", ALICE, RequestContext.REQUEST_ID::get)).isEqualTo("r-1");
    }

    @Test
    void afterCallReturnsTheValuesAreUnboundAgain() {
        handler.placeOrder("r-1", ALICE, "book");
        assertThat(RequestContext.PRINCIPAL.isBound()).isFalse();
        assertThat(RequestContext.REQUEST_ID.isBound()).isFalse();
    }

    @Test
    void nestedRebindingRunsAsSystemAndRestoresTheOuterPrincipal() {
        assertThat(handler.placeOrder("r-1", ALICE, "book")).isEqualTo("order for book placed by alice");
        assertThat(audit.entries()).containsExactly(
                new AuditLog.Entry("r-1", "alice", "place order book"),
                new AuditLog.Entry("r-1", "system", "reserve stock for book"),
                new AuditLog.Entry("r-1", "alice", "confirm order book"));
    }

    @Test
    void getWhenUnboundThrowsAndOrElseGivesTheDefault() {
        assertThatThrownBy(RequestContext.PRINCIPAL::get).isInstanceOf(NoSuchElementException.class);
        assertThat(RequestContext.PRINCIPAL.orElse(Principal.ANONYMOUS)).isEqualTo(Principal.ANONYMOUS);
    }

    @Test
    void aThousandConcurrentRequestsEachSeeOnlyTheirOwnContext() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                int n = i;
                var user = new Principal("user-" + n, Set.of());
                executor.submit(() -> handler.placeOrder("req-" + n, user, "item-" + n));
            }
        }
        Set<AuditLog.Entry> expected = new HashSet<>();
        for (int n = 0; n < 1_000; n++) {
            expected.add(new AuditLog.Entry("req-" + n, "user-" + n, "place order item-" + n));
            expected.add(new AuditLog.Entry("req-" + n, "system", "reserve stock for item-" + n));
            expected.add(new AuditLog.Entry("req-" + n, "user-" + n, "confirm order item-" + n));
        }
        assertThat(Set.copyOf(audit.entries())).isEqualTo(expected);
        assertThat(audit.entries()).hasSize(3_000);
    }

    @Test
    void checkedExceptionFromTheWorkPropagatesUnchanged() {
        var failure = new IOException("disk full");
        assertThatThrownBy(() -> handler.handle("r-1", ALICE, () -> {
            throw failure;
        })).isSameAs(failure);
    }

    @Test
    void entriesAreSortedByRequestIdKeepingTheOrderWithinARequest() {
        handler.placeOrder("r-2", ALICE, "pen");
        handler.placeOrder("r-1", ALICE, "book");
        assertThat(audit.entries()).extracting(AuditLog.Entry::requestId)
                .containsExactlyElementsOf(List.of("r-1", "r-1", "r-1", "r-2", "r-2", "r-2"));
    }

    @Test
    void demoPrintsTheAuditLogSortedByRequestId() {
        assertThat(Demos.output(() -> ScopedRequestDemo.main(new String[0]))).isEqualTo("""
                req-1 | alice  | place order book
                req-1 | system | reserve stock for book
                req-1 | alice  | confirm order book
                req-2 | bob    | place order mug
                req-2 | system | reserve stock for mug
                req-2 | bob    | confirm order mug
                req-3 | carol  | place order pen
                req-3 | system | reserve stock for pen
                req-3 | carol  | confirm order pen
                after the requests: PRINCIPAL bound? false
                PRINCIPAL.get() outside a request -> NoSuchElementException
                PRINCIPAL.orElse(ANONYMOUS) -> anonymous
                """);
    }
}

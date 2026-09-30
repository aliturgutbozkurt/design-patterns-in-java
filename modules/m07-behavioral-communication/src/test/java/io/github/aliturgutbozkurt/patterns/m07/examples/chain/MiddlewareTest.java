package io.github.aliturgutbozkurt.patterns.m07.examples.chain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Handler;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Middleware;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Middlewares;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Pipeline;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Request;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Response;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class MiddlewareTest {

    private final List<String> trace = new ArrayList<>();
    private final AtomicInteger endpointCalls = new AtomicInteger();
    private final Handler endpoint = request -> {
        endpointCalls.incrementAndGet();
        trace.add("endpoint");
        return new Response(200, "ok " + request.header(Middlewares.REQUEST_ID).orElse("-"));
    };

    private Middleware traced(String name) {
        return next -> request -> {
            trace.add(name + " in");
            Response response = next.handle(request);
            trace.add(name + " out");
            return response;
        };
    }

    private static Request authorized(String path) {
        return new Request("GET", path).withHeader("Authorization", "Bearer t1");
    }

    @Test
    void middlewaresRunInDeclaredOrderInAndReverseOrderOut() {
        Pipeline.of(List.of(traced("a"), traced("b"), traced("c")), endpoint).handle(new Request("GET", "/"));
        assertThat(trace).containsExactly("a in", "b in", "c in", "endpoint", "c out", "b out", "a out");
    }

    @Test
    void loggingRecordsTheRequestOnTheWayInAndTheStatusOnTheWayOut() {
        List<String> log = new ArrayList<>();
        Pipeline.of(List.of(Middlewares.logging(log)), endpoint).handle(new Request("POST", "/orders"));
        assertThat(log).containsExactly("-> POST /orders", "<- 200 POST /orders");
    }

    @Test
    void missingOrInvalidTokenGives401AndTheEndpointIsNotInvoked() {
        Handler server = Pipeline.of(List.of(Middlewares.authentication(Set.of("t1"))), endpoint);
        assertThat(server.handle(new Request("GET", "/"))).isEqualTo(new Response(401, "unauthorized"));
        assertThat(server.handle(new Request("GET", "/").withHeader("Authorization", "Bearer nope")).status())
                .isEqualTo(401);
        assertThat(server.handle(new Request("GET", "/").withHeader("Authorization", "t1")).status())
                .isEqualTo(401);
        assertThat(endpointCalls).hasValue(0);
        assertThat(server.handle(authorized("/")).status()).isEqualTo(200);
        assertThat(endpointCalls).hasValue(1);
    }

    @Test
    void errorBoundaryTurnsAnEndpointExceptionInto500WithTheMessage() {
        Handler failing = _ -> {
            throw new IllegalStateException("database down");
        };
        assertThat(Pipeline.of(List.of(Middlewares.errorBoundary()), failing).handle(new Request("GET", "/")))
                .isEqualTo(new Response(500, "internal error: database down"));
    }

    @Test
    void requestIdAddsAHeaderFromTheInjectedSupplier() {
        var ids = new AtomicInteger(41);
        Handler server = Pipeline.of(List.of(Middlewares.requestId(() -> "id-" + ids.incrementAndGet())), endpoint);
        assertThat(server.handle(new Request("GET", "/")).body()).isEqualTo("ok id-42");
        assertThat(server.handle(new Request("GET", "/")).body()).isEqualTo("ok id-43");
    }

    @Test
    void pipelineWithoutMiddlewareIsTheEndpoint() {
        assertThat(Pipeline.of(List.of(), endpoint)).isSameAs(endpoint);
    }

    @Test
    void requestIsImmutable() {
        var request = new Request("GET", "/");
        var withHeader = request.withHeader("A", "1");
        assertThat(request.headers()).isEmpty();
        assertThat(withHeader.headers()).containsEntry("A", "1").isUnmodifiable();
    }

    @Test
    void demoPrintsLogsAndResponses() {
        assertThat(Console.capture(() -> MiddlewareDemo.main(new String[0]))).isEqualTo("""
                -> GET /orders
                <- 200 GET /orders
                   200 orders for req-1
                -> GET /orders
                <- 401 GET /orders
                   401 unauthorized
                -> GET /crash
                <- 500 GET /crash
                   500 internal error: database down
                """);
    }
}

package io.github.aliturgutbozkurt.patterns.m03.examples.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.HttpRequest;
import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.HttpRequest.Method;
import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    static final URI ORDERS = URI.create("https://api.example.com/orders");

    @Test
    void defaultsToGetWithThirtySecondTimeout() {
        HttpRequest request = HttpRequest.newBuilder(ORDERS).build();
        assertThat(request.method()).isEqualTo(Method.GET);
        assertThat(request.timeout()).isEqualTo(Duration.ofSeconds(30));
        assertThat(request.body()).isEmpty();
        assertThat(request.headers()).isEmpty();
    }

    @Test
    void buildsAPostWithHeadersAndBody() {
        HttpRequest request = HttpRequest.newBuilder(ORDERS)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .method(Method.POST, "{\"item\":\"pen\"}")
                .timeout(Duration.ofSeconds(5))
                .build();
        assertThat(request.headers().keySet()).containsExactly("Content-Type", "Accept");
        assertThat(request.body()).contains("{\"item\":\"pen\"}");
        assertThat(request.timeout()).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    void getAndDeleteMustNotHaveABody() {
        assertThatIllegalStateException()
                .isThrownBy(() -> HttpRequest.newBuilder(ORDERS).method(Method.GET, "x").build())
                .withMessage("GET request must not have a body");
        assertThatIllegalStateException()
                .isThrownBy(() -> HttpRequest.newBuilder(ORDERS).method(Method.DELETE, "x").build());
    }

    @Test
    void postAndPutNeedABody() {
        assertThatIllegalStateException()
                .isThrownBy(() -> HttpRequest.newBuilder(ORDERS).method(Method.PUT, null).build())
                .withMessage("PUT request needs a body");
    }

    @Test
    void validatesHeaderNamesAndDuplicates() {
        var builder = HttpRequest.newBuilder(ORDERS);
        assertThatIllegalArgumentException().isThrownBy(() -> builder.header(" ", "x"));
        assertThatIllegalArgumentException().isThrownBy(() -> builder.header("Bad Name", "x"));
        builder.header("Accept", "text/plain");
        assertThatIllegalArgumentException().isThrownBy(() -> builder.header("accept", "text/html"))
                .withMessage("duplicate header: accept");
    }

    @Test
    void timeoutMustBePositive() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> HttpRequest.newBuilder(ORDERS).timeout(Duration.ZERO));
    }

    @Test
    void headersOfTheBuiltRequestAreImmutable() {
        HttpRequest request = HttpRequest.newBuilder(ORDERS).header("Accept", "text/plain").build();
        assertThatThrownBy(() -> request.headers().put("X", "y")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void demoPrintsTheRequests() {
        assertThat(Console.capture(() -> HttpRequestDemo.main(new String[0]))).isEqualTo("""
                GET https://api.example.com/orders (timeout PT30S)
                POST https://api.example.com/orders (timeout PT5S)
                Content-Type: application/json
                Accept: application/json

                {"item":"pen","quantity":2}
                invalid request: GET request must not have a body
                """);
    }
}

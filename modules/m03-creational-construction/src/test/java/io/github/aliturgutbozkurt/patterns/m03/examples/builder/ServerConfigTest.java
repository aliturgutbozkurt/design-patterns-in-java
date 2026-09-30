package io.github.aliturgutbozkurt.patterns.m03.examples.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m03.examples.builder.record.ServerConfig;
import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ServerConfigTest {

    @Test
    void builderAppliesDefaults() {
        assertThat(ServerConfig.builder("localhost").build())
                .isEqualTo(new ServerConfig("localhost", 8080, Duration.ofSeconds(30), false, 100));
    }

    @Test
    void builderOverridesOnlyWhatIsSet() {
        ServerConfig config = ServerConfig.builder("api.example.com").port(443).tls(true).build();
        assertThat(config.port()).isEqualTo(443);
        assertThat(config.tls()).isTrue();
        assertThat(config.maxConnections()).isEqualTo(100);
    }

    @Test
    void validationLivesInOnePlaceTheCompactConstructor() {
        assertThatIllegalArgumentException().isThrownBy(() -> ServerConfig.builder("h").port(70_000).build())
                .withMessage("port must be in 1..65535: 70000");
        ServerConfig valid = ServerConfig.builder("h").build();
        assertThatIllegalArgumentException().isThrownBy(() -> valid.withPort(0))
                .withMessage("port must be in 1..65535: 0");
        assertThatIllegalArgumentException().isThrownBy(() -> ServerConfig.builder(" ").build());
        assertThatIllegalArgumentException().isThrownBy(() -> ServerConfig.builder("h").maxConnections(0).build());
        assertThatIllegalArgumentException().isThrownBy(() -> ServerConfig.builder("h").timeout(Duration.ZERO).build());
    }

    @Test
    void withCopiesLeaveTheOriginalUnchanged() {
        ServerConfig original = ServerConfig.builder("localhost").build();
        ServerConfig secure = original.withTls(true).withPort(8443);
        assertThat(original.tls()).isFalse();
        assertThat(original.port()).isEqualTo(8080);
        assertThat(secure).isEqualTo(new ServerConfig("localhost", 8443, Duration.ofSeconds(30), true, 100));
    }

    @Test
    void demoPrintsTheConfigurations() {
        assertThat(Console.capture(() -> ServerConfigDemo.main(new String[0]))).isEqualTo("""
                defaults: ServerConfig[host=localhost, port=8080, timeout=PT30S, tls=false, maxConnections=100]
                production: ServerConfig[host=api.example.com, port=443, timeout=PT10S, tls=true, maxConnections=500]
                staging copy: ServerConfig[host=api.example.com, port=8443, timeout=PT10S, tls=true, maxConnections=500]
                """);
    }
}

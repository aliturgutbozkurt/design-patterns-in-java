package io.github.aliturgutbozkurt.patterns.m11.examples.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.InMemoryReportRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.MiniContainer;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.ReportFormatter;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.ReportRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.ReportService;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class MiniContainerTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-30T10:00:00Z"), ZoneOffset.UTC);

    /** Test graph with a cycle: A needs B, B needs A. */
    public static final class A {
        public A(B b) {}
    }

    /** The other half of the cycle. */
    public static final class B {
        public B(A a) {}
    }

    /** Ambiguous for a container: which constructor should it call? */
    public static final class TwoConstructors {
        public TwoConstructors() {}

        public TwoConstructors(Clock clock) {}
    }

    /** A constructor that fails. */
    public static final class Exploding {
        public Exploding() {
            throw new IllegalArgumentException("boom");
        }
    }

    private MiniContainer reporting() {
        return new MiniContainer()
                .bindInstance(Clock.class, CLOCK)
                .bind(ReportRepository.class, InMemoryReportRepository.class);
    }

    @Test
    void resolvesAThreeLevelGraph() {
        ReportService service = reporting().get(ReportService.class);
        assertThat(service.dailyReport()).isEqualTo("sales 2026-09-30: book=3, mug=1, pen=5");
    }

    @Test
    void singletonReturnsTheSameInstance() {
        MiniContainer container = reporting().singleton(ReportRepository.class);
        assertThat(container.get(ReportRepository.class)).isSameAs(container.get(ReportRepository.class));
    }

    @Test
    void unboundConcreteClassIsCreatedFreshEachTime() {
        MiniContainer container = reporting();
        assertThat(container.get(ReportFormatter.class)).isNotSameAs(container.get(ReportFormatter.class));
        assertThat(container.get(ReportRepository.class)).isNotSameAs(container.get(ReportRepository.class));
    }

    @Test
    void boundInstanceIsReturnedAsIs() {
        assertThat(reporting().get(Clock.class)).isSameAs(CLOCK);
    }

    @Test
    void missingBindingNamesTheTypeAndTheResolutionPath() {
        MiniContainer container = new MiniContainer().bindInstance(Clock.class, CLOCK);
        assertThatIllegalStateException().isThrownBy(() -> container.get(ReportService.class))
                .withMessage("no binding for ReportRepository (resolving ReportService -> ReportRepository)");
    }

    @Test
    void cycleIsReportedWithThePath() {
        assertThatIllegalStateException().isThrownBy(() -> new MiniContainer().get(A.class))
                .withMessage("dependency cycle: A -> B -> A");
    }

    @Test
    void classWithTwoPublicConstructorsIsRejected() {
        assertThatIllegalStateException().isThrownBy(() -> new MiniContainer().get(TwoConstructors.class))
                .withMessage("TwoConstructors must have exactly one public constructor, found 2");
    }

    @Test
    void failingConstructorIsReportedWithItsCause() {
        assertThatIllegalStateException().isThrownBy(() -> new MiniContainer().get(Exploding.class))
                .withMessage("constructor of Exploding failed")
                .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bindingToAnAbstractTypeIsRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MiniContainer().bind(Clock.class, Clock.class))
                .withMessage("Clock cannot be instantiated");
    }

    @Test
    void rejectsNullArguments() {
        var container = new MiniContainer();
        assertThatNullPointerException().isThrownBy(() -> container.get(null));
        assertThatNullPointerException().isThrownBy(() -> container.bind(null, InMemoryReportRepository.class));
        assertThatNullPointerException().isThrownBy(() -> container.bind(ReportRepository.class, null));
        assertThatNullPointerException().isThrownBy(() -> container.bindInstance(Clock.class, null));
        assertThatNullPointerException().isThrownBy(() -> container.singleton(null));
    }

    @Test
    void demoPrintsResolutionScopesAndARunTimeError() {
        assertThat(Console.capture(() -> MiniContainerDemo.main(new String[0]))).isEqualTo("""
                sales 2026-09-30: book=3, mug=1, pen=5
                repository is a singleton: true
                formatter is created fresh: true
                forgotten binding, found only at run time: no binding for ReportRepository (resolving ReportService -> ReportRepository)
                """);
    }
}

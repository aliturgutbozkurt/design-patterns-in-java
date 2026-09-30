package io.github.aliturgutbozkurt.patterns.m03.examples.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m03.examples.builder.step.Query;
import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class QueryTest {

    static boolean hasBuildMethod(Class<?> step) {
        return Arrays.stream(step.getMethods()).map(Method::getName).anyMatch("build"::equals);
    }

    @Test
    void rendersAMinimalQuery() {
        assertThat(Query.builder().select("name", "email").from("users").build().toSql())
                .isEqualTo("SELECT name, email FROM users");
    }

    @Test
    void rendersEveryOptionalClause() {
        Query query = Query.builder()
                .select("name", "email")
                .from("users")
                .where("active = true")
                .where("age >= 18")
                .orderByDescending("created_at")
                .limit(10)
                .build();
        assertThat(query.toSql())
                .isEqualTo("SELECT name, email FROM users WHERE active = true AND age >= 18 ORDER BY created_at DESC LIMIT 10");
    }

    @Test
    void selectAllRendersAStar() {
        assertThat(Query.builder().selectAll().from("orders").orderBy("id").build().toSql())
                .isEqualTo("SELECT * FROM orders ORDER BY id");
    }

    @Test
    void theEarlyStepsHaveNoBuildMethod() {
        assertThat(hasBuildMethod(Query.SelectStep.class)).isFalse();
        assertThat(hasBuildMethod(Query.FromStep.class)).isFalse();
        assertThat(hasBuildMethod(Query.QueryStep.class)).isTrue();
    }

    @Test
    void validatesItsInputs() {
        assertThatIllegalArgumentException().isThrownBy(() -> Query.builder().select());
        assertThatIllegalArgumentException().isThrownBy(() -> Query.builder().select("name; DROP TABLE users"));
        assertThatIllegalArgumentException().isThrownBy(() -> Query.builder().selectAll().from(" "));
        assertThatIllegalArgumentException().isThrownBy(() -> Query.builder().selectAll().from("t").limit(0));
    }

    @Test
    void builtQueriesAreIndependentOfLaterSteps() {
        Query.QueryStep step = Query.builder().selectAll().from("users");
        Query first = step.build();
        step.where("id = 1");
        assertThat(first.toSql()).isEqualTo("SELECT * FROM users");
        assertThat(step.build().toSql()).isEqualTo("SELECT * FROM users WHERE id = 1");
    }

    @Test
    void demoPrintsTheQueries() {
        assertThat(Console.capture(() -> QueryDemo.main(new String[0]))).isEqualTo("""
                SELECT name, email FROM users WHERE active = true ORDER BY name LIMIT 20
                SELECT * FROM orders WHERE total > 100 AND status = 'PAID' ORDER BY total DESC
                """);
    }
}

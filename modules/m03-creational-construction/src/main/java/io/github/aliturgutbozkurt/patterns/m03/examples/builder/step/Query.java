package io.github.aliturgutbozkurt.patterns.m03.examples.builder.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Step (type-safe) builder: each call returns an interface that offers only the legal next calls, so
 * {@code Query.builder().select("a").build()} does not even compile — {@code build()} appears only after
 * {@code from(...)}.
 *
 * <p>A teaching example: conditions are plain text, so this is <em>not</em> protected against SQL injection; real code
 * uses prepared statements with parameters.
 *
 * @see "m03 lesson, section Builder — step builder"
 */
public final class Query {

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    /** Step 1: choose columns. */
    public interface SelectStep {
        FromStep select(String... columns);

        FromStep selectAll();
    }

    /** Step 2: choose the table. */
    public interface FromStep {
        QueryStep from(String table);
    }

    /** Step 3: optional clauses, then {@code build()}. */
    public interface QueryStep {
        QueryStep where(String condition);

        QueryStep orderBy(String column);

        QueryStep orderByDescending(String column);

        QueryStep limit(int rows);

        Query build();
    }

    private final List<String> columns;
    private final String table;
    private final List<String> conditions;
    private final String orderBy;
    private final int limit;

    private Query(Steps steps) {
        columns = List.copyOf(steps.columns);
        table = steps.table;
        conditions = List.copyOf(steps.conditions);
        orderBy = steps.orderBy;
        limit = steps.limit;
    }

    public static SelectStep builder() {
        return new Steps();
    }

    public String toSql() {
        var sql = new StringBuilder("SELECT ").append(String.join(", ", columns)).append(" FROM ").append(table);
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        if (orderBy != null) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
        }
        return sql.toString();
    }

    /** One mutable object plays all three steps; callers only ever see it through the step interfaces. */
    private static final class Steps implements SelectStep, FromStep, QueryStep {

        private final List<String> columns = new ArrayList<>();
        private String table;
        private final List<String> conditions = new ArrayList<>();
        private String orderBy;
        private int limit;

        @Override
        public FromStep select(String... names) {
            if (names.length == 0) {
                throw new IllegalArgumentException("select at least one column");
            }
            for (String name : names) {
                columns.add(identifier(name));
            }
            return this;
        }

        @Override
        public FromStep selectAll() {
            columns.add("*");
            return this;
        }

        @Override
        public QueryStep from(String name) {
            table = identifier(name);
            return this;
        }

        @Override
        public QueryStep where(String condition) {
            if (condition.isBlank()) {
                throw new IllegalArgumentException("condition must not be blank");
            }
            conditions.add(condition);
            return this;
        }

        @Override
        public QueryStep orderBy(String column) {
            orderBy = identifier(column);
            return this;
        }

        @Override
        public QueryStep orderByDescending(String column) {
            orderBy = identifier(column) + " DESC";
            return this;
        }

        @Override
        public QueryStep limit(int rows) {
            if (rows <= 0) {
                throw new IllegalArgumentException("limit must be positive: " + rows);
            }
            limit = rows;
            return this;
        }

        @Override
        public Query build() {
            return new Query(this);
        }

        private static String identifier(String name) {
            Objects.requireNonNull(name, "name");
            if (!IDENTIFIER.matcher(name).matches()) {
                throw new IllegalArgumentException("not an identifier: '" + name + "'");
            }
            return name;
        }
    }
}

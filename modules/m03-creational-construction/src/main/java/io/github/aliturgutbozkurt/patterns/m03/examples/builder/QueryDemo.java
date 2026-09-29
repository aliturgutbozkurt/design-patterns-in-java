package io.github.aliturgutbozkurt.patterns.m03.examples.builder;

import io.github.aliturgutbozkurt.patterns.m03.examples.builder.step.Query;

/** Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/builder/QueryDemo.java} */
public final class QueryDemo {

    private QueryDemo() {}

    public static void main(String[] args) {
        Query activeUsers = Query.builder()
                .select("name", "email")
                .from("users")
                .where("active = true")
                .orderBy("name")
                .limit(20)
                .build();
        System.out.println(activeUsers.toSql());

        // Query.builder().select("name").build();   // does not compile: FromStep has no build()
        Query bigOrders = Query.builder()
                .selectAll()
                .from("orders")
                .where("total > 100")
                .where("status = 'PAID'")
                .orderByDescending("total")
                .build();
        System.out.println(bigOrders.toSql());
    }
}
